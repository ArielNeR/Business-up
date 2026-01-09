package com.businessup.ui.clients

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.businessup.BusinessUpApp
import com.businessup.data.model.Cliente
import com.businessup.data.model.CuentaBanco
import com.businessup.utils.Resource
import com.businessup.utils.generateRandomId
import kotlinx.coroutines.launch

class ClientesViewModel(application: Application) : AndroidViewModel(application) {

    private val clienteRepository = (application as BusinessUpApp).clienteRepository

    val allClientes: LiveData<List<Cliente>> = clienteRepository.allClientes

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    val searchResults: LiveData<List<Cliente>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) {
            clienteRepository.allClientes
        } else {
            clienteRepository.search(query)
        }
    }

    private val _saveResult = MutableLiveData<Resource<Cliente>>()
    val saveResult: LiveData<Resource<Cliente>> = _saveResult

    private val _deleteResult = MutableLiveData<Resource<Boolean>>()
    val deleteResult: LiveData<Resource<Boolean>> = _deleteResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addCliente(
        nombre: String,
        numerosContacto: List<String>,
        correos: List<String>,
        cuentasBanco: List<CuentaBanco>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _saveResult.value = Resource.Loading()

            try {
                if (nombre.isBlank()) {
                    _saveResult.value = Resource.Error("El nombre es requerido")
                    return@launch
                }

                if (clienteRepository.existsByNombre(nombre)) {
                    _saveResult.value = Resource.Error("Ya existe un cliente con ese nombre")
                    return@launch
                }

                val cliente = Cliente(
                    idCliente = generateRandomId(),
                    nombre = nombre,
                    numerosContacto = numerosContacto.filter { it.isNotBlank() },
                    correos = correos.filter { it.isNotBlank() },
                    cuentasBanco = cuentasBanco
                )

                val id = clienteRepository.insert(cliente)
                val savedCliente = clienteRepository.getById(id)
                _saveResult.value = Resource.Success(savedCliente!!)

            } catch (e: Exception) {
                _saveResult.value = Resource.Error("Error al guardar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            _saveResult.value = Resource.Loading()

            try {
                clienteRepository.update(cliente)
                _saveResult.value = Resource.Success(cliente)
            } catch (e: Exception) {
                _saveResult.value = Resource.Error("Error al actualizar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCliente(cliente: Cliente) {
        viewModelScope.launch {
            _isLoading.value = true
            _deleteResult.value = Resource.Loading()

            try {
                clienteRepository.delete(cliente)
                _deleteResult.value = Resource.Success(true)
            } catch (e: Exception) {
                _deleteResult.value = Resource.Error("Error al eliminar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearSaveResult() {
        _saveResult.value = Resource.Success(Cliente(idCliente = "", nombre = ""))
    }
}
