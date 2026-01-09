package com.businessup.ui.inventory

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.businessup.R
import com.businessup.data.model.InventarioItem
import com.businessup.databinding.ActivityAddProductBinding
import com.businessup.utils.Resource
import com.businessup.utils.gone
import com.businessup.utils.toast
import com.businessup.utils.visible

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val viewModel: InventarioViewModel by viewModels()

    private var editingItem: InventarioItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingItem = intent.getParcelableExtra("item")

        setupToolbar()
        setupUnitDropdown()
        setupClickListeners()
        observeViewModel()

        editingItem?.let { populateFields(it) }

        if (editingItem != null) {
            binding.toolbar.title = "Editar Producto"
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupUnitDropdown() {
        val units = listOf(
            getString(R.string.unit_unit),
            getString(R.string.unit_kilogram),
            getString(R.string.unit_gram),
            getString(R.string.unit_liter),
            getString(R.string.unit_milliliter),
            getString(R.string.unit_meter),
            getString(R.string.unit_centimeter),
            getString(R.string.unit_dozen),
            getString(R.string.unit_pack)
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, units)
        binding.actvUnit.setAdapter(adapter)
        binding.actvUnit.setText(units[0], false)
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveProduct()
        }
    }

    private fun observeViewModel() {
        viewModel.saveProductoResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressBar.visible()
                    binding.btnSave.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.gone()
                    binding.btnSave.isEnabled = true
                    toast("Producto guardado")
                    finish()
                }
                is Resource.Error -> {
                    binding.progressBar.gone()
                    binding.btnSave.isEnabled = true
                    toast(result.message ?: "Error al guardar")
                }
            }
        }
    }

    private fun populateFields(item: InventarioItem) {
        binding.etName.setText(item.nombre)
        binding.actvUnit.setText(item.unidadMedida, false)
        binding.etSalePrice.setText(item.precio.toString())
        binding.etQuantity.setText(item.cantidad.toString())
        binding.etBarcode.setText(item.codigoBarra)
    }

    private fun saveProduct() {
        val nombre = binding.etName.text.toString().trim()
        val unidad = binding.actvUnit.text.toString().trim()
        val precioVentaStr = binding.etSalePrice.text.toString()
        val precioProveedorStr = binding.etProviderPrice.text.toString()
        val cantidadStr = binding.etQuantity.text.toString()
        val codigoBarra = binding.etBarcode.text.toString().trim()

        if (nombre.isBlank()) {
            toast("El nombre es requerido")
            return
        }

        val precioVenta = precioVentaStr.toDoubleOrNull() ?: 0.0
        val precioProveedor = precioProveedorStr.toDoubleOrNull() ?: 0.0
        val cantidad = cantidadStr.toIntOrNull() ?: 0

        viewModel.addProducto(nombre, unidad, precioVenta, precioProveedor, cantidad, codigoBarra)
    }
}
