package com.businessup.ui.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.businessup.R
import com.businessup.databinding.ActivityEditProfileBinding
import com.businessup.utils.Resource
import com.businessup.utils.gone
import com.businessup.utils.toast
import com.businessup.utils.visible
import java.io.ByteArrayOutputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    private var selectedPhotoBytes: ByteArray? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                binding.ivProfilePhoto.setImageBitmap(bitmap)

                // Convert to byte array
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream)
                selectedPhotoBytes = outputStream.toByteArray()

            } catch (e: Exception) {
                toast("Error al cargar imagen")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupClickListeners() {
        binding.cardProfilePhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            saveProfile()
        }
    }

    private fun observeViewModel() {
        viewModel.usuario.observe(this) { usuario ->
            usuario?.let {
                binding.etName.setText(it.nombre)
                binding.etEmail.setText(it.correo)

                it.fotoPerfil?.let { bytes ->
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    binding.ivProfilePhoto.setImageBitmap(bitmap)
                }
            }
        }

        viewModel.updateResult.observe(this) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.progressBar.visible()
                    binding.btnSave.isEnabled = false
                }
                is Resource.Success -> {
                    binding.progressBar.gone()
                    binding.btnSave.isEnabled = true
                    toast(getString(R.string.edit_profile_success))
                    finish()
                }
                is Resource.Error -> {
                    binding.progressBar.gone()
                    binding.btnSave.isEnabled = true
                    toast(result.message ?: "Error")
                }
            }
        }
    }

    private fun saveProfile() {
        val nombre = binding.etName.text.toString().trim()
        val correo = binding.etEmail.text.toString().trim()

        viewModel.updateProfile(nombre, correo, selectedPhotoBytes)
    }
}
