package com.businessup.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.businessup.BusinessUpApp
import com.businessup.data.model.Usuario
import com.businessup.utils.Resource
import com.businessup.utils.SessionManager
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val usuarioRepository = (application as BusinessUpApp).usuarioRepository
    private val sessionManager = SessionManager.getInstance(application)

    private val _usuario = MutableLiveData<Usuario?>()
    val usuario: LiveData<Usuario?> = _usuario

    private val _updateResult = MutableLiveData<Resource<Usuario>>()
    val updateResult: LiveData<Resource<Usuario>> = _updateResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadUserData()
    }

    fun loadUserData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = sessionManager.getUserId()
                if (userId != -1L) {
                    val user = usuarioRepository.getById(userId)
                    _usuario.value = user
                } else {
                    _usuario.value = sessionManager.getUser()
                }
            } catch (e: Exception) {
                _usuario.value = sessionManager.getUser()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(nombre: String, correo: String, fotoPerfil: ByteArray? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _updateResult.value = Resource.Loading()

            try {
                val currentUser = _usuario.value
                if (currentUser == null) {
                    _updateResult.value = Resource.Error("Usuario no encontrado")
                    return@launch
                }

                // Validation
                if (nombre.isBlank()) {
                    _updateResult.value = Resource.Error("El nombre es requerido")
                    return@launch
                }

                if (correo.isBlank()) {
                    _updateResult.value = Resource.Error("El correo es requerido")
                    return@launch
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                    _updateResult.value = Resource.Error("El correo no es válido")
                    return@launch
                }

                // Check if new name is already taken (if changed)
                if (nombre != currentUser.nombre) {
                    if (usuarioRepository.existsByNombre(nombre)) {
                        _updateResult.value = Resource.Error("El nombre de usuario ya existe")
                        return@launch
                    }
                }

                // Check if new email is already taken (if changed)
                if (correo != currentUser.correo) {
                    val existingEmail = usuarioRepository.getByCorreo(correo)
                    if (existingEmail != null && existingEmail.id != currentUser.id) {
                        _updateResult.value = Resource.Error("El correo ya está registrado")
                        return@launch
                    }
                }

                val updatedUser = currentUser.copy(
                    nombre = nombre,
                    correo = correo,
                    fotoPerfil = fotoPerfil ?: currentUser.fotoPerfil
                )

                usuarioRepository.update(updatedUser)
                sessionManager.updateUserData(updatedUser)
                _usuario.value = updatedUser
                _updateResult.value = Resource.Success(updatedUser)

            } catch (e: Exception) {
                _updateResult.value = Resource.Error("Error al actualizar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }
}
