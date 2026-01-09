package com.businessup.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.businessup.data.model.Servicio

@Dao
interface ServicioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(servicio: Servicio): Long

    @Update
    suspend fun update(servicio: Servicio)

    @Delete
    suspend fun delete(servicio: Servicio)

    @Query("SELECT * FROM servicios WHERE id = :id")
    suspend fun getById(id: Long): Servicio?

    @Query("SELECT * FROM servicios WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): Servicio?

    @Query("SELECT EXISTS(SELECT 1 FROM servicios WHERE nombre = :nombre)")
    suspend fun existsByNombre(nombre: String): Boolean

    @Query("SELECT * FROM servicios ORDER BY nombre ASC")
    fun getAllLiveData(): LiveData<List<Servicio>>

    @Query("SELECT * FROM servicios ORDER BY nombre ASC")
    suspend fun getAll(): List<Servicio>

    @Query("SELECT * FROM servicios WHERE nombre LIKE '%' || :query || '%' OR codigoBarra LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun search(query: String): LiveData<List<Servicio>>

    @Query("SELECT * FROM servicios ORDER BY nombre ASC LIMIT :limit")
    fun getTop(limit: Int): LiveData<List<Servicio>>

    @Query("SELECT COUNT(*) FROM servicios")
    suspend fun getCount(): Int
}
