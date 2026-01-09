package com.businessup.data.repository

import androidx.lifecycle.LiveData
import com.businessup.data.dao.ServicioDao
import com.businessup.data.model.Servicio

class ServicioRepository(private val servicioDao: ServicioDao) {

    val allServicios: LiveData<List<Servicio>> = servicioDao.getAllLiveData()

    suspend fun insert(servicio: Servicio): Long {
        return servicioDao.insert(servicio)
    }

    suspend fun update(servicio: Servicio) {
        servicioDao.update(servicio)
    }

    suspend fun delete(servicio: Servicio) {
        servicioDao.delete(servicio)
    }

    suspend fun getById(id: Long): Servicio? {
        return servicioDao.getById(id)
    }

    suspend fun getByNombre(nombre: String): Servicio? {
        return servicioDao.getByNombre(nombre)
    }

    suspend fun existsByNombre(nombre: String): Boolean {
        return servicioDao.existsByNombre(nombre)
    }

    fun search(query: String): LiveData<List<Servicio>> {
        return servicioDao.search(query)
    }

    fun getTop(limit: Int): LiveData<List<Servicio>> {
        return servicioDao.getTop(limit)
    }

    suspend fun getAll(): List<Servicio> {
        return servicioDao.getAll()
    }

    suspend fun getCount(): Int {
        return servicioDao.getCount()
    }
}
