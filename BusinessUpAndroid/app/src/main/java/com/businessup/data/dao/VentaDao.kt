package com.businessup.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.businessup.data.model.Venta

@Dao
interface VentaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(venta: Venta): Long

    @Update
    suspend fun update(venta: Venta)

    @Delete
    suspend fun delete(venta: Venta)

    @Query("SELECT * FROM ventas WHERE id = :id")
    suspend fun getById(id: Long): Venta?

    @Query("SELECT * FROM ventas WHERE numeroFactura = :numeroFactura LIMIT 1")
    suspend fun getByNumeroFactura(numeroFactura: Int): Venta?

    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    fun getAllLiveData(): LiveData<List<Venta>>

    @Query("SELECT * FROM ventas ORDER BY fecha DESC")
    suspend fun getAll(): List<Venta>

    // Ventas no pagadas (cuentas por cobrar)
    @Query("SELECT * FROM ventas WHERE pagado = 0 ORDER BY fecha DESC")
    fun getVentasNoPagadasLiveData(): LiveData<List<Venta>>

    @Query("SELECT * FROM ventas WHERE pagado = 0 ORDER BY fecha DESC")
    suspend fun getVentasNoPagadas(): List<Venta>

    // Top 5 ventas no pagadas más recientes (para Home)
    @Query("SELECT * FROM ventas WHERE pagado = 0 ORDER BY fecha DESC LIMIT 5")
    fun getTopVentasNoPagadas(): LiveData<List<Venta>>

    // Ventas pagadas
    @Query("SELECT * FROM ventas WHERE pagado = 1 ORDER BY fecha DESC")
    fun getVentasPagadasLiveData(): LiveData<List<Venta>>

    @Query("SELECT * FROM ventas WHERE pagado = 1 ORDER BY fecha DESC")
    suspend fun getVentasPagadas(): List<Venta>

    // Ventas por fecha
    @Query("SELECT * FROM ventas WHERE fecha >= :startDate AND fecha <= :endDate ORDER BY fecha DESC")
    fun getVentasByFecha(startDate: Long, endDate: Long): LiveData<List<Venta>>

    @Query("SELECT * FROM ventas WHERE fecha >= :startDate AND fecha <= :endDate ORDER BY fecha DESC")
    suspend fun getVentasByFechaSync(startDate: Long, endDate: Long): List<Venta>

    // Ventas pagadas por fecha
    @Query("SELECT * FROM ventas WHERE pagado = 1 AND fecha >= :startDate AND fecha <= :endDate ORDER BY fecha DESC")
    suspend fun getVentasPagadasByFecha(startDate: Long, endDate: Long): List<Venta>

    // Buscar ventas
    @Query("SELECT * FROM ventas WHERE clienteNombre LIKE '%' || :query || '%' ORDER BY fecha DESC")
    fun search(query: String): LiveData<List<Venta>>

    // Marcar como pagada
    @Query("UPDATE ventas SET pagado = 1 WHERE id = :id")
    suspend fun marcarComoPagada(id: Long)

    // Obtener siguiente número de factura
    @Query("SELECT COALESCE(MAX(numeroFactura), 0) + 1 FROM ventas")
    suspend fun getNextNumeroFactura(): Int

    // Total de ventas no pagadas
    @Query("SELECT COUNT(*) FROM ventas WHERE pagado = 0")
    suspend fun getCountVentasNoPagadas(): Int

    // Total de ventas pagadas
    @Query("SELECT COUNT(*) FROM ventas WHERE pagado = 1")
    suspend fun getCountVentasPagadas(): Int
}
