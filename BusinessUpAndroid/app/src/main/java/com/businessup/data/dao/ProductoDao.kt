package com.businessup.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.businessup.data.model.Producto

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(producto: Producto): Long

    @Update
    suspend fun update(producto: Producto)

    @Delete
    suspend fun delete(producto: Producto)

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getById(id: Long): Producto?

    @Query("SELECT * FROM productos WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): Producto?

    @Query("SELECT EXISTS(SELECT 1 FROM productos WHERE nombre = :nombre)")
    suspend fun existsByNombre(nombre: String): Boolean

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun getAllLiveData(): LiveData<List<Producto>>

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    suspend fun getAll(): List<Producto>

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%' OR codigoBarra LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun search(query: String): LiveData<List<Producto>>

    @Query("SELECT * FROM productos ORDER BY nombre ASC LIMIT :limit")
    fun getTop(limit: Int): LiveData<List<Producto>>

    @Query("UPDATE productos SET cantidadTotal = cantidadTotal - :cantidad WHERE id = :id")
    suspend fun decrementarStock(id: Long, cantidad: Int)

    @Query("UPDATE productos SET cantidadTotal = cantidadTotal + :cantidad WHERE id = :id")
    suspend fun incrementarStock(id: Long, cantidad: Int)

    @Query("SELECT SUM(precioVenta * cantidadTotal) FROM productos")
    suspend fun getValorTotalInventario(): Double?

    @Query("SELECT COUNT(*) FROM productos")
    suspend fun getCount(): Int
}
