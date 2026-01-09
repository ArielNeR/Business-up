package com.businessup.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.businessup.data.model.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(usuario: Usuario): Long

    @Update
    suspend fun update(usuario: Usuario)

    @Delete
    suspend fun delete(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getById(id: Long): Usuario?

    @Query("SELECT * FROM usuarios WHERE nombre = :nombre LIMIT 1")
    suspend fun getByNombre(nombre: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE nombre = :nombre AND contrasena = :contrasena LIMIT 1")
    suspend fun login(nombre: String, contrasena: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun getByCorreo(correo: String): Usuario?

    @Query("SELECT EXISTS(SELECT 1 FROM usuarios WHERE nombre = :nombre)")
    suspend fun existsByNombre(nombre: String): Boolean

    @Query("SELECT * FROM usuarios")
    fun getAllLiveData(): LiveData<List<Usuario>>

    @Query("SELECT * FROM usuarios")
    suspend fun getAll(): List<Usuario>
}
