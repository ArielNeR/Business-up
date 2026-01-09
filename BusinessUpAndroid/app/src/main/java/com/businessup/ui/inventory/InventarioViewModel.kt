package com.businessup.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.businessup.BusinessUpApp
import com.businessup.data.model.InventarioItem
import com.businessup.data.model.Producto
import com.businessup.data.model.Servicio
import com.businessup.data.model.TipoInventario
import com.businessup.utils.Resource
import kotlinx.coroutines.launch

class InventarioViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as BusinessUpApp
    private val productoRepository = app.productoRepository
    private val servicioRepository = app.servicioRepository
    private val inventarioRepository = app.inventarioRepository

    val allInventario: LiveData<List<InventarioItem>> = inventarioRepository.getAllInventario()

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    val searchResults: LiveData<List<InventarioItem>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) {
            inventarioRepository.getAllInventario()
        } else {
            inventarioRepository.searchInventario(query)
        }
    }

    private val _saveProductoResult = MutableLiveData<Resource<Producto>>()
    val saveProductoResult: LiveData<Resource<Producto>> = _saveProductoResult

    private val _saveServicioResult = MutableLiveData<Resource<Servicio>>()
    val saveServicioResult: LiveData<Resource<Servicio>> = _saveServicioResult

    private val _deleteResult = MutableLiveData<Resource<Boolean>>()
    val deleteResult: LiveData<Resource<Boolean>> = _deleteResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addProducto(
        nombre: String,
        unidadMedida: String,
        precioVenta: Double,
        precioProveedor: Double,
        cantidad: Int,
        codigoBarra: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _saveProductoResult.value = Resource.Loading()

            try {
                if (nombre.isBlank()) {
                    _saveProductoResult.value = Resource.Error("El nombre es requerido")
                    return@launch
                }

                if (productoRepository.existsByNombre(nombre)) {
                    _saveProductoResult.value = Resource.Error("Ya existe un producto con ese nombre")
                    return@launch
                }

                val producto = Producto(
                    nombre = nombre,
                    unidadMedida = unidadMedida,
                    precioVenta = precioVenta,
                    precioProveedor = precioProveedor,
                    cantidadTotal = cantidad,
                    codigoBarra = codigoBarra
                )

                val id = productoRepository.insert(producto)
                val savedProducto = productoRepository.getById(id)
                _saveProductoResult.value = Resource.Success(savedProducto!!)

            } catch (e: Exception) {
                _saveProductoResult.value = Resource.Error("Error al guardar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addServicio(
        nombre: String,
        valor: Double,
        codigoBarra: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _saveServicioResult.value = Resource.Loading()

            try {
                if (nombre.isBlank()) {
                    _saveServicioResult.value = Resource.Error("El nombre es requerido")
                    return@launch
                }

                if (servicioRepository.existsByNombre(nombre)) {
                    _saveServicioResult.value = Resource.Error("Ya existe un servicio con ese nombre")
                    return@launch
                }

                val servicio = Servicio(
                    nombre = nombre,
                    valor = valor,
                    codigoBarra = codigoBarra
                )

                val id = servicioRepository.insert(servicio)
                val savedServicio = servicioRepository.getById(id)
                _saveServicioResult.value = Resource.Success(savedServicio!!)

            } catch (e: Exception) {
                _saveServicioResult.value = Resource.Error("Error al guardar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProducto(producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                productoRepository.update(producto)
                _saveProductoResult.value = Resource.Success(producto)
            } catch (e: Exception) {
                _saveProductoResult.value = Resource.Error("Error al actualizar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateServicio(servicio: Servicio) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                servicioRepository.update(servicio)
                _saveServicioResult.value = Resource.Success(servicio)
            } catch (e: Exception) {
                _saveServicioResult.value = Resource.Error("Error al actualizar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteItem(item: InventarioItem) {
        viewModelScope.launch {
            _isLoading.value = true
            _deleteResult.value = Resource.Loading()

            try {
                when (item.tipo) {
                    TipoInventario.PRODUCTO -> {
                        val producto = productoRepository.getById(item.id)
                        producto?.let { productoRepository.delete(it) }
                    }
                    TipoInventario.SERVICIO -> {
                        val servicio = servicioRepository.getById(item.id)
                        servicio?.let { servicioRepository.delete(it) }
                    }
                }
                _deleteResult.value = Resource.Success(true)
            } catch (e: Exception) {
                _deleteResult.value = Resource.Error("Error al eliminar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateStock(item: InventarioItem, newQuantity: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (item.tipo == TipoInventario.PRODUCTO) {
                    val producto = productoRepository.getById(item.id)
                    producto?.let {
                        productoRepository.update(it.copy(cantidadTotal = newQuantity))
                    }
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}
