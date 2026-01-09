package com.businessup.data.repository

import androidx.lifecycle.LiveData
import com.businessup.data.dao.ProductoDao
import com.businessup.data.model.Producto

class ProductoRepository(private val productoDao: ProductoDao) {

    val allProductos: LiveData<List<Producto>> = productoDao.getAllLiveData()

    suspend fun insert(producto: Producto): Long {
        return productoDao.insert(producto)
    }

    suspend fun update(producto: Producto) {
        productoDao.update(producto)
    }

    suspend fun delete(producto: Producto) {
        productoDao.delete(producto)
    }

    suspend fun getById(id: Long): Producto? {
        return productoDao.getById(id)
    }

    suspend fun getByNombre(nombre: String): Producto? {
        return productoDao.getByNombre(nombre)
    }

    suspend fun existsByNombre(nombre: String): Boolean {
        return productoDao.existsByNombre(nombre)
    }

    fun search(query: String): LiveData<List<Producto>> {
        return productoDao.search(query)
    }

    fun getTop(limit: Int): LiveData<List<Producto>> {
        return productoDao.getTop(limit)
    }

    suspend fun getAll(): List<Producto> {
        return productoDao.getAll()
    }

    suspend fun decrementarStock(id: Long, cantidad: Int) {
        productoDao.decrementarStock(id, cantidad)
    }

    suspend fun incrementarStock(id: Long, cantidad: Int) {
        productoDao.incrementarStock(id, cantidad)
    }

    suspend fun getValorTotalInventario(): Double {
        return productoDao.getValorTotalInventario() ?: 0.0
    }

    suspend fun getCount(): Int {
        return productoDao.getCount()
    }
}
