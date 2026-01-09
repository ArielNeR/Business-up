package com.businessup.ui.login

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

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository = (application as BusinessUpApp).usuarioRepository

    private val _loginResult = MutableLiveData<Resource<Usuario>>()
    val loginResult: LiveData<Resource<Usuario>> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(nombre: String, contrasena: String) {
        if (nombre.isBlank()) {
            _loginResult.value = Resource.Error("El nombre de usuario es requerido")
            return
        }

        if (contrasena.isBlank()) {
            _loginResult.value = Resource.Error("La contraseña es requerida")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _loginResult.value = Resource.Loading()

            try {
                val hashedPassword = contrasena.toMD5()
                val usuario = usuarioRepository.login(nombre, hashedPassword)

                if (usuario != null) {
                    _loginResult.value = Resource.Success(usuario)
                } else {
                    // Check if user exists
                    val userExists = usuarioRepository.existsByNombre(nombre)
                    if (userExists) {
                        _loginResult.value = Resource.Error("Contraseña incorrecta")
                    } else {
                        _loginResult.value = Resource.Error("El usuario no existe")
                    }
                }
            } catch (e: Exception) {
                _loginResult.value = Resource.Error("Error al iniciar sesión: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
