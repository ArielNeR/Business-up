package com.businessup.ui.sales

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.businessup.BusinessUpApp
import com.businessup.data.model.Venta
import com.businessup.utils.Resource
import kotlinx.coroutines.launch

enum class SalesListType {
    ALL,
    UNPAID,  // Cuentas por cobrar
    PAID     // Ventas pagadas
}

class SalesListViewModel(application: Application) : AndroidViewModel(application) {

    private val ventaRepository = (application as BusinessUpApp).ventaRepository

    private val _listType = MutableLiveData<SalesListType>(SalesListType.ALL)
    val listType: LiveData<SalesListType> = _listType

    val ventas: LiveData<List<Venta>> = _listType.switchMap { type ->
        when (type) {
            SalesListType.ALL -> ventaRepository.allVentas
            SalesListType.UNPAID -> ventaRepository.ventasNoPagadas
            SalesListType.PAID -> ventaRepository.ventasPagadas
        }
    }

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    val searchResults: LiveData<List<Venta>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) {
            ventas
        } else {
            ventaRepository.search(query)
        }
    }

    private val _markPaidResult = MutableLiveData<Resource<Boolean>>()
    val markPaidResult: LiveData<Resource<Boolean>> = _markPaidResult

    private val _deleteResult = MutableLiveData<Resource<Boolean>>()
    val deleteResult: LiveData<Resource<Boolean>> = _deleteResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setListType(type: SalesListType) {
        _listType.value = type
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun markAsPaid(venta: Venta) {
        viewModelScope.launch {
            _isLoading.value = true
            _markPaidResult.value = Resource.Loading()

            try {
                ventaRepository.marcarComoPagada(venta.id)
                _markPaidResult.value = Resource.Success(true)
            } catch (e: Exception) {
                _markPaidResult.value = Resource.Error("Error al marcar como pagada: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteVenta(venta: Venta) {
        viewModelScope.launch {
            _isLoading.value = true
            _deleteResult.value = Resource.Loading()

            try {
                ventaRepository.delete(venta)
                _deleteResult.value = Resource.Success(true)
            } catch (e: Exception) {
                _deleteResult.value = Resource.Error("Error al eliminar: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getVentaById(id: Long): Venta? {
        return ventaRepository.getById(id)
    }
}
