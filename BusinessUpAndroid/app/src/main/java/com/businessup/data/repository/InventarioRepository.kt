package com.businessup.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.businessup.data.dao.ProductoDao
import com.businessup.data.dao.ServicioDao
import com.businessup.data.model.InventarioItem
import com.businessup.data.model.Producto
import com.businessup.data.model.Servicio

class InventarioRepository(
    private val productoDao: ProductoDao,
    private val servicioDao: ServicioDao
) {

    fun getAllInventario(): LiveData<List<InventarioItem>> {
        val mediatorLiveData = MediatorLiveData<List<InventarioItem>>()

        var productos: List<Producto> = emptyList()
        var servicios: List<Servicio> = emptyList()

        mediatorLiveData.addSource(productoDao.getAllLiveData()) { list ->
            productos = list
            mediatorLiveData.value = combineInventario(productos, servicios)
        }

        mediatorLiveData.addSource(servicioDao.getAllLiveData()) { list ->
            servicios = list
            mediatorLiveData.value = combineInventario(productos, servicios)
        }

        return mediatorLiveData
    }

    fun getTopInventario(limit: Int): LiveData<List<InventarioItem>> {
        val mediatorLiveData = MediatorLiveData<List<InventarioItem>>()

        var productos: List<Producto> = emptyList()
        var servicios: List<Servicio> = emptyList()

        mediatorLiveData.addSource(productoDao.getTop(limit)) { list ->
            productos = list
            val combined = combineInventario(productos, servicios)
            mediatorLiveData.value = combined.take(limit)
        }

        mediatorLiveData.addSource(servicioDao.getTop(limit)) { list ->
            servicios = list
            val combined = combineInventario(productos, servicios)
            mediatorLiveData.value = combined.take(limit)
        }

        return mediatorLiveData
    }

    fun searchInventario(query: String): LiveData<List<InventarioItem>> {
        val mediatorLiveData = MediatorLiveData<List<InventarioItem>>()

        var productos: List<Producto> = emptyList()
        var servicios: List<Servicio> = emptyList()

        mediatorLiveData.addSource(productoDao.search(query)) { list ->
            productos = list
            mediatorLiveData.value = combineInventario(productos, servicios)
        }

        mediatorLiveData.addSource(servicioDao.search(query)) { list ->
            servicios = list
            mediatorLiveData.value = combineInventario(productos, servicios)
        }

        return mediatorLiveData
    }

    private fun combineInventario(productos: List<Producto>, servicios: List<Servicio>): List<InventarioItem> {
        val inventarioProductos = productos.map { InventarioItem.fromProducto(it) }
        val inventarioServicios = servicios.map { InventarioItem.fromServicio(it) }
        return (inventarioProductos + inventarioServicios).sortedBy { it.nombre }
    }

    suspend fun getAllInventarioSync(): List<InventarioItem> {
        val productos = productoDao.getAll()
        val servicios = servicioDao.getAll()
        return combineInventario(productos, servicios)
    }

    suspend fun getValorTotalInventario(): Double {
        return productoDao.getValorTotalInventario() ?: 0.0
    }

    suspend fun getCount(): Int {
        return productoDao.getCount() + servicioDao.getCount()
    }
}
