package com.businessup.ui.inventory

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.businessup.data.model.InventarioItem
import com.businessup.databinding.ActivityAddServiceBinding
import com.businessup.utils.Resource
import com.businessup.utils.gone
import com.businessup.utils.toast
import com.businessup.utils.visible

class AddServiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddServiceBinding
    private val viewModel: InventarioViewModel by viewModels()

    private var editingItem: InventarioItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddServiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingItem = intent.getParcelableExtra("item")

        setupToolbar()
        setupClickListeners()
        observeViewModel()

        editingItem?.let { populateFields(it) }

        if (editingItem != null) {
            binding.toolbar.title = "Editar Servicio"
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupClickListeners() {
        binding.btnSave.setOnClickListener {
            saveService()
        }
    }

    private fun observeViewModel() {
        viewModel.saveServicioResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressBar.visible()
                    binding.btnSave.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.gone()
                    binding.btnSave.isEnabled = true
                    toast("Servicio guardado")
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
        binding.etValue.setText(item.precio.toString())
        binding.etBarcode.setText(item.codigoBarra)
    }

    private fun saveService() {
        val nombre = binding.etName.text.toString().trim()
        val valorStr = binding.etValue.text.toString()
        val codigoBarra = binding.etBarcode.text.toString().trim()

        if (nombre.isBlank()) {
            toast("El nombre es requerido")
            return
        }

        val valor = valorStr.toDoubleOrNull() ?: 0.0

        viewModel.addServicio(nombre, valor, codigoBarra)
    }
}
