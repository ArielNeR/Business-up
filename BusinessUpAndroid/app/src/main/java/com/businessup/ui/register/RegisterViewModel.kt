package com.businessup.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.businessup.BusinessUpApp
import com.businessup.data.model.Usuario
import com.businessup.utils.Resource
import com.businessup.utils.toMD5
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository = (application as BusinessUpApp).usuarioRepository

    private val _registerResult = MutableLiveData<Resource<Usuario>>()
    val registerResult: LiveData<Resource<Usuario>> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun register(nombre: String, correo: String, contrasena: String, confirmarContrasena: String) {
        // Validations
        if (nombre.isBlank()) {
            _registerResult.value = Resource.Error("El nombre de usuario es requerido")
            return
        }

        if (correo.isBlank()) {
            _registerResult.value = Resource.Error("El correo es requerido")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _registerResult.value = Resource.Error("El correo no es válido")
            return
        }

        if (contrasena.isBlank()) {
            _registerResult.value = Resource.Error("La contraseña es requerida")
            return
        }

        if (contrasena.length < 6) {
            _registerResult.value = Resource.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        if (contrasena != confirmarContrasena) {
            _registerResult.value = Resource.Error("Las contraseñas no coinciden")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _registerResult.value = Resource.Loading()

            try {
                // Check if user already exists
                if (usuarioRepository.existsByNombre(nombre)) {
                    _registerResult.value = Resource.Error("El nombre de usuario ya existe")
                    _isLoading.value = false
                    return@launch
                }

                // Check if email already exists
                val existingEmail = usuarioRepository.getByCorreo(correo)
                if (existingEmail != null) {
                    _registerResult.value = Resource.Error("El correo ya está registrado")
                    _isLoading.value = false
                    return@launch
                }

                // Create new user
                val hashedPassword = contrasena.toMD5()
                val newUser = Usuario(
                    nombre = nombre,
                    correo = correo,
                    contrasena = hashedPassword
                )

                val userId = usuarioRepository.insert(newUser)
                val createdUser = usuarioRepository.getById(userId)

                if (createdUser != null) {
                    _registerResult.value = Resource.Success(createdUser)
                } else {
                    _registerResult.value = Resource.Error("Error al crear el usuario")
                }
            } catch (e: Exception) {
                _registerResult.value = Resource.Error("Error al registrar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
