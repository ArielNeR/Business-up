package com.businessup.data.repository

import androidx.lifecycle.LiveData
import com.businessup.data.dao.UsuarioDao
import com.businessup.data.model.Usuario

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    val allUsuarios: LiveData<List<Usuario>> = usuarioDao.getAllLiveData()

    suspend fun insert(usuario: Usuario): Long {
        return usuarioDao.insert(usuario)
    }

    suspend fun update(usuario: Usuario) {
        usuarioDao.update(usuario)
    }

    suspend fun delete(usuario: Usuario) {
        usuarioDao.delete(usuario)
    }

    suspend fun getById(id: Long): Usuario? {
        return usuarioDao.getById(id)
    }

    suspend fun getByNombre(nombre: String): Usuario? {
        return usuarioDao.getByNombre(nombre)
    }

    suspend fun login(nombre: String, contrasena: String): Usuario? {
        return usuarioDao.login(nombre, contrasena)
    }

    suspend fun existsByNombre(nombre: String): Boolean {
        return usuarioDao.existsByNombre(nombre)
    }

    suspend fun getByCorreo(correo: String): Usuario? {
        return usuarioDao.getByCorreo(correo)
    }
}
