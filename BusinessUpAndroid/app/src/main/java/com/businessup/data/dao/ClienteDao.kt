package com.businessup.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.businessup.data.model.Cliente

@Dao
interface ClienteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cliente: Cliente): Long

    @Update
    suspend fun update(cliente: Cliente)

    @Delete
    suspend fun delete(cliente: Cliente)

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun getById(id: Long): Cliente?

    @Query("SELECT * FROM clientes WHERE idCliente = :idCliente LIMIT 1")
    suspend fun getByIdCliente(idCliente: String): Cliente?

    @Query("SELECT * FROM clientes WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): Cliente?

    @Query("SELECT EXISTS(SELECT 1 FROM clientes WHERE nombre = :nombre)")
    suspend fun existsByNombre(nombre: String): Boolean

    @Query("SELECT * FROM clientes ORDER BY nombre ASC")
    fun getAllLiveData(): LiveData<List<Cliente>>

    @Query("SELECT * FROM clientes ORDER BY nombre ASC")
    suspend fun getAll(): List<Cliente>

    @Query("SELECT * FROM clientes WHERE nombre LIKE '%' || :query || '%' OR idCliente LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun search(query: String): LiveData<List<Cliente>>

    @Query("SELECT * FROM clientes ORDER BY nombre ASC LIMIT :limit")
    fun getTop(limit: Int): LiveData<List<Cliente>>

    @Query("SELECT COUNT(*) FROM clientes")
    suspend fun getCount(): Int
}
