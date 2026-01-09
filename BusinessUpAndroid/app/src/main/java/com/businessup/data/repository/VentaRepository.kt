package com.businessup.data.repository

import androidx.lifecycle.LiveData
import com.businessup.data.dao.VentaDao
import com.businessup.data.model.Venta

class VentaRepository(private val ventaDao: VentaDao) {

    val allVentas: LiveData<List<Venta>> = ventaDao.getAllLiveData()
    val ventasNoPagadas: LiveData<List<Venta>> = ventaDao.getVentasNoPagadasLiveData()
    val ventasPagadas: LiveData<List<Venta>> = ventaDao.getVentasPagadasLiveData()
    val topVentasNoPagadas: LiveData<List<Venta>> = ventaDao.getTopVentasNoPagadas()

    suspend fun insert(venta: Venta): Long {
        return ventaDao.insert(venta)
    }

    suspend fun update(venta: Venta) {
        ventaDao.update(venta)
    }

    suspend fun delete(venta: Venta) {
        ventaDao.delete(venta)
    }

    suspend fun getById(id: Long): Venta? {
        return ventaDao.getById(id)
    }

    suspend fun getByNumeroFactura(numeroFactura: Int): Venta? {
        return ventaDao.getByNumeroFactura(numeroFactura)
    }

    suspend fun getAll(): List<Venta> {
        return ventaDao.getAll()
    }

    suspend fun getVentasNoPagadas(): List<Venta> {
        return ventaDao.getVentasNoPagadas()
    }

    suspend fun getVentasPagadas(): List<Venta> {
        return ventaDao.getVentasPagadas()
    }

    fun getVentasByFecha(startDate: Long, endDate: Long): LiveData<List<Venta>> {
        return ventaDao.getVentasByFecha(startDate, endDate)
    }

    suspend fun getVentasByFechaSync(startDate: Long, endDate: Long): List<Venta> {
        return ventaDao.getVentasByFechaSync(startDate, endDate)
    }

    suspend fun getVentasPagadasByFecha(startDate: Long, endDate: Long): List<Venta> {
        return ventaDao.getVentasPagadasByFecha(startDate, endDate)
    }

    fun search(query: String): LiveData<List<Venta>> {
        return ventaDao.search(query)
    }

    suspend fun marcarComoPagada(id: Long) {
        ventaDao.marcarComoPagada(id)
    }

    suspend fun getNextNumeroFactura(): Int {
        return ventaDao.getNextNumeroFactura()
    }

    suspend fun getCountVentasNoPagadas(): Int {
        return ventaDao.getCountVentasNoPagadas()
    }

    suspend fun getCountVentasPagadas(): Int {
        return ventaDao.getCountVentasPagadas()
    }
}
