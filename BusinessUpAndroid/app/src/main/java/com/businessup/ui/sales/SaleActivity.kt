package com.businessup.ui.sales

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.businessup.R
import com.businessup.adapters.CarritoAdapter
import com.businessup.data.model.Cliente
import com.businessup.data.model.InventarioItem
import com.businessup.databinding.ActivitySaleBinding
import com.businessup.ui.clients.SelectClientActivity
import com.businessup.ui.inventory.SelectInventoryActivity
import com.businessup.ui.pdf.PdfGenerator
import com.businessup.ui.pdf.PdfViewerActivity
import com.businessup.utils.Resource
import com.businessup.utils.SessionManager
import com.businessup.utils.gone
import com.businessup.utils.toCurrency
import com.businessup.utils.toast
import com.businessup.utils.visible
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SaleActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaleBinding
    private val viewModel: VentaViewModel by viewModels()
    private lateinit var carritoAdapter: CarritoAdapter
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val selectClientLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val cliente = result.data?.getParcelableExtra<Cliente>("cliente")
            cliente?.let { viewModel.setCliente(it) }
        }
    }

    private val selectInventoryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val item = result.data?.getParcelableExtra<InventarioItem>("item")
            val quantity = result.data?.getIntExtra("quantity", 1) ?: 1
            item?.let { viewModel.addToCart(it, quantity) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check if client was passed from Home
        intent.getParcelableExtra<Cliente>("cliente")?.let {
            viewModel.setCliente(it)
        }

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        // Set initial date
        updateDateDisplay(System.currentTimeMillis())
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        carritoAdapter = CarritoAdapter(
            onQuantityChange = { item, newQuantity ->
                viewModel.updateCartItemQuantity(item, newQuantity)
            },
            onDelete = { item ->
                viewModel.removeFromCart(item)
            }
        )
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(this@SaleActivity)
            adapter = carritoAdapter
        }
    }

    private fun setupClickListeners() {
        binding.cardDate.setOnClickListener {
            showDatePicker()
        }

        binding.cardClient.setOnClickListener {
            selectClientLauncher.launch(Intent(this, SelectClientActivity::class.java))
        }

        binding.cardPayment.setOnClickListener {
            showPaymentMethodDialog()
        }

        binding.switchDebt.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setEsDeuda(isChecked)
        }

        binding.btnAddItem.setOnClickListener {
            selectInventoryLauncher.launch(Intent(this, SelectInventoryActivity::class.java))
        }

        binding.btnInvoice.setOnClickListener {
            processInvoice()
        }
    }

    private fun observeViewModel() {
        viewModel.selectedCliente.observe(this) { cliente ->
            binding.tvClient.text = cliente?.nombre ?: getString(R.string.sale_select_client)
        }

        viewModel.metodoPago.observe(this) { metodo ->
            binding.tvPaymentMethod.text = metodo
        }

        viewModel.carritoItems.observe(this) { items ->
            carritoAdapter.submitList(items?.toList())
            if (items.isNullOrEmpty()) {
                binding.tvEmptyCart.visible()
                binding.rvCart.gone()
            } else {
                binding.tvEmptyCart.gone()
                binding.rvCart.visible()
            }
        }

        viewModel.total.observe(this) { total ->
            binding.tvTotal.text = total.toCurrency()
        }

        viewModel.saveResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.loadingOverlay.visible()
                }
                is Resource.Success -> {
                    binding.loadingOverlay.gone()
                    toast(getString(R.string.sale_success))

                    // Show/share PDF
                    viewModel.pdfBytes.value?.let { pdfBytes ->
                        val intent = Intent(this, PdfViewerActivity::class.java)
                        intent.putExtra("pdf_bytes", pdfBytes)
                        intent.putExtra("title", "Factura #${result.data?.numeroFactura}")
                        startActivity(intent)
                    }

                    finish()
                }
                is Resource.Error -> {
                    binding.loadingOverlay.gone()
                    toast(result.message ?: getString(R.string.error_generic))
                }
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val dateMillis = calendar.timeInMillis
                viewModel.setFechaVenta(dateMillis)
                updateDateDisplay(dateMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateDisplay(dateMillis: Long) {
        binding.tvDate.text = dateFormat.format(Date(dateMillis))
    }

    private fun showPaymentMethodDialog() {
        val methods = arrayOf(
            getString(R.string.payment_cash),
            getString(R.string.payment_card),
            getString(R.string.payment_transfer),
            getString(R.string.payment_other)
        )

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_select_payment)
            .setItems(methods) { _, which ->
                viewModel.setMetodoPago(methods[which])
            }
            .show()
    }

    private fun processInvoice() {
        val cliente = viewModel.selectedCliente.value
        if (cliente == null) {
            toast(getString(R.string.sale_error_no_client))
            return
        }

        val items = viewModel.carritoItems.value
        if (items.isNullOrEmpty()) {
            toast(getString(R.string.sale_error_empty_cart))
            return
        }

        // Generate PDF
        val sessionManager = SessionManager.getInstance(this)
        val usuario = sessionManager.getUser()

        val pdfGenerator = PdfGenerator(this)
        val pdfBytes = pdfGenerator.generateInvoice(
            numeroFactura = 0, // Will be set by ViewModel
            cliente = cliente,
            items = items,
            total = viewModel.total.value ?: 0.0,
            fecha = viewModel.fechaVenta.value ?: System.currentTimeMillis(),
            metodoPago = viewModel.metodoPago.value ?: "Efectivo",
            usuario = usuario
        )

        viewModel.processVenta(pdfBytes)
    }
}
