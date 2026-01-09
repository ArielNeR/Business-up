package com.businessup.ui.sales

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.businessup.R
import com.businessup.adapters.VentaListAdapter
import com.businessup.data.model.Venta
import com.businessup.databinding.ActivitySalesListBinding
import com.businessup.ui.pdf.PdfViewerActivity
import com.businessup.utils.Resource
import com.businessup.utils.gone
import com.businessup.utils.toast
import com.businessup.utils.visible

class SalesListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesListBinding
    private val viewModel: SalesListViewModel by viewModels()
    private lateinit var adapter: VentaListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val typeString = intent.getStringExtra("type") ?: SalesListType.ALL.name
        val type = SalesListType.valueOf(typeString)
        viewModel.setListType(type)

        setupToolbar(type)
        setupRecyclerView()
        setupSearch()
        observeViewModel()
    }

    private fun setupToolbar(type: SalesListType) {
        binding.toolbar.title = when (type) {
            SalesListType.ALL -> getString(R.string.sales_list_title)
            SalesListType.UNPAID -> getString(R.string.balance_accounts_receivable)
            SalesListType.PAID -> getString(R.string.balance_sales_made)
        }
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = VentaListAdapter(
            onViewInvoice = { venta ->
                venta.facturaPdf?.let { pdfBytes ->
                    val intent = Intent(this, PdfViewerActivity::class.java)
                    intent.putExtra("pdf_bytes", pdfBytes)
                    intent.putExtra("title", "Factura #${venta.numeroFactura}")
                    startActivity(intent)
                }
            },
            onMarkPaid = { venta ->
                showMarkPaidConfirmation(venta)
            }
        )
        binding.rvSales.apply {
            layoutManager = LinearLayoutManager(this@SalesListActivity)
            adapter = this@SalesListActivity.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
        }
    }

    private fun observeViewModel() {
        viewModel.ventas.observe(this) { ventas ->
            adapter.submitList(ventas)
            adapter.setShowMarkPaidButton(viewModel.listType.value == SalesListType.UNPAID)

            if (ventas.isEmpty()) {
                binding.layoutEmpty.visible()
                binding.rvSales.gone()
            } else {
                binding.layoutEmpty.gone()
                binding.rvSales.visible()
            }
        }

        viewModel.markPaidResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    toast("Venta marcada como pagada")
                }
                is Resource.Error -> {
                    toast(result.message ?: "Error")
                }
                else -> {}
            }
        }
    }

    private fun showMarkPaidConfirmation(venta: Venta) {
        AlertDialog.Builder(this)
            .setTitle(R.string.sales_mark_paid)
            .setMessage(R.string.dialog_confirm_mark_paid)
            .setPositiveButton(R.string.btn_accept) { _, _ ->
                viewModel.markAsPaid(venta)
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }
}
