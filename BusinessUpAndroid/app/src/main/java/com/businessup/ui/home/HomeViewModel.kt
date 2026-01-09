package com.businessup.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.businessup.BusinessUpApp
import com.businessup.data.model.Cliente
import com.businessup.data.model.InventarioItem
import com.businessup.data.model.Venta
import com.businessup.utils.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as BusinessUpApp
    private val clienteRepository = app.clienteRepository
    private val inventarioRepository = app.inventarioRepository
    private val ventaRepository = app.ventaRepository

    // LiveData for clients preview
    private val _clientesState = MutableLiveData<UiState<List<Cliente>>>()
    val clientesState: LiveData<UiState<List<Cliente>>> = _clientesState

    // LiveData for inventory preview
    private val _inventarioState = MutableLiveData<UiState<List<InventarioItem>>>()
    val inventarioState: LiveData<UiState<List<InventarioItem>>> = _inventarioState

    // LiveData for pending sales (accounts receivable)
    private val _ventasPendientesState = MutableLiveData<UiState<List<Venta>>>()
    val ventasPendientesState: LiveData<UiState<List<Venta>>> = _ventasPendientesState

    // Direct LiveData from repository
    val topClientes: LiveData<List<Cliente>> = clienteRepository.getTop(5)
    val topInventario: LiveData<List<InventarioItem>> = inventarioRepository.getTopInventario(5)
    val ventasNoPagadas: LiveData<List<Venta>> = ventaRepository.ventasNoPagadas

    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isRefreshing.value = true

            // Set loading state for all sections
            _clientesState.value = UiState.Loading
            _inventarioState.value = UiState.Loading
            _ventasPendientesState.value = UiState.Loading

            // Simulate loading delay for skeleton animation
            delay(500)

            try {
                // Load clients
                val clientes = clienteRepository.getAll().take(5)
                _clientesState.value = if (clientes.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(clientes)
                }

                // Load inventory
                val inventario = inventarioRepository.getAllInventarioSync().take(5)
                _inventarioState.value = if (inventario.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(inventario)
                }

                // Load pending sales
                val ventasPendientes = ventaRepository.getVentasNoPagadas()
                    .sortedByDescending { it.total }
                    .take(5)
                _ventasPendientesState.value = if (ventasPendientes.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(ventasPendientes)
                }

            } catch (e: Exception) {
                _clientesState.value = UiState.Error(e.message ?: "Error loading data")
                _inventarioState.value = UiState.Error(e.message ?: "Error loading data")
                _ventasPendientesState.value = UiState.Error(e.message ?: "Error loading data")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun refresh() {
        loadData()
    }
}
