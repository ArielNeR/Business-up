package com.businessup.ui.sales

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.businessup.BusinessUpApp
import com.businessup.data.model.CarritoItem
import com.businessup.data.model.Cliente
import com.businessup.data.model.InventarioItem
import com.businessup.data.model.TipoInventario
import com.businessup.data.model.Venta
import com.businessup.utils.Resource
import kotlinx.coroutines.launch

class VentaViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as BusinessUpApp
    private val ventaRepository = app.ventaRepository
    private val productoRepository = app.productoRepository

    // Selected client
    private val _selectedCliente = MutableLiveData<Cliente?>()
    val selectedCliente: LiveData<Cliente?> = _selectedCliente

    // Payment method
    private val _metodoPago = MutableLiveData<String>("Efectivo")
    val metodoPago: LiveData<String> = _metodoPago

    // Is debt (not paid)
    private val _esDeuda = MutableLiveData<Boolean>(false)
    val esDeuda: LiveData<Boolean> = _esDeuda

    // Selected date
    private val _fechaVenta = MutableLiveData<Long>(System.currentTimeMillis())
    val fechaVenta: LiveData<Long> = _fechaVenta

    // Cart items
    private val _carritoItems = MutableLiveData<MutableList<CarritoItem>>(mutableListOf())
    val carritoItems: LiveData<MutableList<CarritoItem>> = _carritoItems

    // Total
    private val _total = MutableLiveData<Double>(0.0)
    val total: LiveData<Double> = _total

    // Save result
    private val _saveResult = MutableLiveData<Resource<Venta>>()
    val saveResult: LiveData<Resource<Venta>> = _saveResult

    // Generated PDF
    private val _pdfBytes = MutableLiveData<ByteArray?>()
    val pdfBytes: LiveData<ByteArray?> = _pdfBytes

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setCliente(cliente: Cliente) {
        _selectedCliente.value = cliente
    }

    fun setMetodoPago(metodo: String) {
        _metodoPago.value = metodo
    }

    fun setEsDeuda(esDeuda: Boolean) {
        _esDeuda.value = esDeuda
    }

    fun setFechaVenta(fecha: Long) {
        _fechaVenta.value = fecha
    }

    fun addToCart(item: InventarioItem, cantidad: Int) {
        val currentList = _carritoItems.value ?: mutableListOf()

        // Check if item already exists in cart
        val existingIndex = currentList.indexOfFirst { it.itemId == item.id && it.tipo == item.tipo }

        if (existingIndex != -1) {
            // Update quantity
            currentList[existingIndex].cantidad += cantidad
        } else {
            // Add new item
            currentList.add(
                CarritoItem(
                    itemId = item.id,
                    nombre = item.nombre,
                    tipo = item.tipo,
                    precio = item.precio,
                    cantidad = cantidad,
                    unidadMedida = item.unidadMedida
                )
            )
        }

        _carritoItems.value = currentList
        calculateTotal()
    }

    fun updateCartItemQuantity(item: CarritoItem, newQuantity: Int) {
        val currentList = _carritoItems.value ?: mutableListOf()
        val index = currentList.indexOfFirst { it.itemId == item.itemId && it.tipo == item.tipo }

        if (index != -1) {
            if (newQuantity <= 0) {
                currentList.removeAt(index)
            } else {
                currentList[index].cantidad = newQuantity
            }
            _carritoItems.value = currentList
            calculateTotal()
        }
    }

    fun removeFromCart(item: CarritoItem) {
        val currentList = _carritoItems.value ?: mutableListOf()
        currentList.removeAll { it.itemId == item.itemId && it.tipo == item.tipo }
        _carritoItems.value = currentList
        calculateTotal()
    }

    fun clearCart() {
        _carritoItems.value = mutableListOf()
        _total.value = 0.0
    }

    private fun calculateTotal() {
        val items = _carritoItems.value ?: emptyList()
        _total.value = items.sumOf { it.subtotal }
    }

    fun processVenta(pdfBytes: ByteArray?) {
        viewModelScope.launch {
            _isLoading.value = true
            _saveResult.value = Resource.Loading()

            try {
                val cliente = _selectedCliente.value
                if (cliente == null) {
                    _saveResult.value = Resource.Error("Debe seleccionar un cliente")
                    return@launch
                }

                val items = _carritoItems.value
                if (items.isNullOrEmpty()) {
                    _saveResult.value = Resource.Error("El carrito está vacío")
                    return@launch
                }

                // Get next invoice number
                val numeroFactura = ventaRepository.getNextNumeroFactura()

                // Create sale
                val venta = Venta(
                    numeroFactura = numeroFactura,
                    clienteId = cliente.id,
                    clienteNombre = cliente.nombre,
                    productos = items.toList(),
                    pagado = !(_esDeuda.value ?: false),
                    fecha = _fechaVenta.value ?: System.currentTimeMillis(),
                    metodoPago = _metodoPago.value ?: "Efectivo",
                    facturaPdf = pdfBytes
                )

                // Save sale
                val ventaId = ventaRepository.insert(venta)

                // Update product stock
                for (item in items) {
                    if (item.tipo == TipoInventario.PRODUCTO) {
                        productoRepository.decrementarStock(item.itemId, item.cantidad)
                    }
                }

                val savedVenta = ventaRepository.getById(ventaId)
                _pdfBytes.value = pdfBytes
                _saveResult.value = Resource.Success(savedVenta!!)

                // Clear cart after successful sale
                clearCart()
                _selectedCliente.value = null
                _esDeuda.value = false
                _metodoPago.value = "Efectivo"

            } catch (e: Exception) {
                _saveResult.value = Resource.Error("Error al procesar la venta: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        clearCart()
        _selectedCliente.value = null
        _esDeuda.value = false
        _metodoPago.value = "Efectivo"
        _fechaVenta.value = System.currentTimeMillis()
        _saveResult.value = Resource.Success(Venta(numeroFactura = 0, clienteId = 0, clienteNombre = ""))
    }
}
