package com.businessup.data.repository

import androidx.lifecycle.LiveData
import com.businessup.data.dao.ClienteDao
import com.businessup.data.model.Cliente

class ClienteRepository(private val clienteDao: ClienteDao) {

    val allClientes: LiveData<List<Cliente>> = clienteDao.getAllLiveData()

    suspend fun insert(cliente: Cliente): Long {
        return clienteDao.insert(cliente)
    }

    suspend fun update(cliente: Cliente) {
        clienteDao.update(cliente)
    }

    suspend fun delete(cliente: Cliente) {
        clienteDao.delete(cliente)
    }

    suspend fun getById(id: Long): Cliente? {
        return clienteDao.getById(id)
    }

    suspend fun getByIdCliente(idCliente: String): Cliente? {
        return clienteDao.getByIdCliente(idCliente)
    }

    suspend fun getByNombre(nombre: String): Cliente? {
        return clienteDao.getByNombre(nombre)
    }

    suspend fun existsByNombre(nombre: String): Boolean {
        return clienteDao.existsByNombre(nombre)
    }

    fun search(query: String): LiveData<List<Cliente>> {
        return clienteDao.search(query)
    }

    fun getTop(limit: Int): LiveData<List<Cliente>> {
        return clienteDao.getTop(limit)
    }

    suspend fun getAll(): List<Cliente> {
        return clienteDao.getAll()
    }

    suspend fun getCount(): Int {
        return clienteDao.getCount()
    }
}
