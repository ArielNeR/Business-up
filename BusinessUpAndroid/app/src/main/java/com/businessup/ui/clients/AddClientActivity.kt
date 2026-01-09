package com.businessup.ui.clients

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.businessup.R
import com.businessup.data.model.Cliente
import com.businessup.data.model.CuentaBanco
import com.businessup.databinding.ActivityAddClientBinding
import com.businessup.utils.Resource
import com.businessup.utils.gone
import com.businessup.utils.toast
import com.businessup.utils.visible

class AddClientActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddClientBinding
    private val viewModel: ClientesViewModel by viewModels()

    private val phoneViews = mutableListOf<EditText>()
    private val emailViews = mutableListOf<EditText>()
    private val bankViews = mutableListOf<Pair<EditText, EditText>>()

    private var editingCliente: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddClientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editingCliente = intent.getParcelableExtra("cliente")

        setupToolbar()
        setupClickListeners()
        observeViewModel()

        // Add initial fields
        addPhoneField()
        addEmailField()

        // If editing, populate fields
        editingCliente?.let { populateFields(it) }

        // Update title if editing
        if (editingCliente != null) {
            binding.toolbar.title = "Editar Cliente"
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupClickListeners() {
        binding.btnAddPhone.setOnClickListener { addPhoneField() }
        binding.btnAddEmail.setOnClickListener { addEmailField() }
        binding.btnAddBank.setOnClickListener { addBankField() }

        binding.btnSave.setOnClickListener {
            saveClient()
        }
    }

    private fun observeViewModel() {
        viewModel.saveResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressBar.visible()
                    binding.btnSave.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.gone()
                    binding.btnSave.isEnabled = true
                    toast("Cliente guardado")
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

    private fun addPhoneField(value: String = "") {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
        }

        val editText = EditText(this).apply {
            hint = getString(R.string.client_phone)
            inputType = android.text.InputType.TYPE_CLASS_PHONE
            setText(value)
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
        }

        val deleteBtn = ImageButton(this).apply {
            setImageResource(R.drawable.ic_close)
            setBackgroundResource(android.R.color.transparent)
            setOnClickListener {
                binding.layoutPhones.removeView(container)
                phoneViews.remove(editText)
            }
        }

        container.addView(editText)
        container.addView(deleteBtn)
        binding.layoutPhones.addView(container)
        phoneViews.add(editText)
    }

    private fun addEmailField(value: String = "") {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 8)
            }
        }

        val editText = EditText(this).apply {
            hint = getString(R.string.client_email)
            inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            setText(value)
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            )
        }

        val deleteBtn = ImageButton(this).apply {
            setImageResource(R.drawable.ic_close)
            setBackgroundResource(android.R.color.transparent)
            setOnClickListener {
                binding.layoutEmails.removeView(container)
                emailViews.remove(editText)
            }
        }

        container.addView(editText)
        container.addView(deleteBtn)
        binding.layoutEmails.addView(container)
        emailViews.add(editText)
    }

    private fun addBankField(tipo: String = "", codigo: String = "") {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 8, 0, 16)
            }
        }

        val tipoEdit = EditText(this).apply {
            hint = getString(R.string.client_bank_type)
            setText(tipo)
        }

        val codigoEdit = EditText(this).apply {
            hint = getString(R.string.client_bank_code)
            setText(codigo)
        }

        val deleteBtn = ImageButton(this).apply {
            setImageResource(R.drawable.ic_close)
            setBackgroundResource(android.R.color.transparent)
            setOnClickListener {
                binding.layoutBanks.removeView(container)
                bankViews.removeAll { it.first == tipoEdit }
            }
        }

        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        row.addView(tipoEdit, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(deleteBtn)

        container.addView(row)
        container.addView(codigoEdit)
        binding.layoutBanks.addView(container)
        bankViews.add(Pair(tipoEdit, codigoEdit))
    }

    private fun populateFields(cliente: Cliente) {
        binding.etName.setText(cliente.nombre)

        // Clear default fields
        binding.layoutPhones.removeAllViews()
        binding.layoutEmails.removeAllViews()
        phoneViews.clear()
        emailViews.clear()

        // Add existing phones
        cliente.numerosContacto.forEach { phone ->
            addPhoneField(phone)
        }
        if (cliente.numerosContacto.isEmpty()) addPhoneField()

        // Add existing emails
        cliente.correos.forEach { email ->
            addEmailField(email)
        }
        if (cliente.correos.isEmpty()) addEmailField()

        // Add existing bank accounts
        cliente.cuentasBanco.forEach { cuenta ->
            addBankField(cuenta.tipoCuenta, cuenta.codigoCuenta)
        }
    }

    private fun saveClient() {
        val nombre = binding.etName.text.toString().trim()

        if (nombre.isBlank()) {
            toast("El nombre es requerido")
            return
        }

        val phones = phoneViews.map { it.text.toString().trim() }.filter { it.isNotBlank() }
        val emails = emailViews.map { it.text.toString().trim() }.filter { it.isNotBlank() }
        val banks = bankViews.map {
            CuentaBanco(
                tipoCuenta = it.first.text.toString().trim(),
                codigoCuenta = it.second.text.toString().trim()
            )
        }.filter { it.tipoCuenta.isNotBlank() || it.codigoCuenta.isNotBlank() }

        if (editingCliente != null) {
            val updatedCliente = editingCliente!!.copy(
                nombre = nombre,
                numerosContacto = phones,
                correos = emails,
                cuentasBanco = banks
            )
            viewModel.updateCliente(updatedCliente)
        } else {
            viewModel.addCliente(nombre, phones, emails, banks)
        }
    }
}
