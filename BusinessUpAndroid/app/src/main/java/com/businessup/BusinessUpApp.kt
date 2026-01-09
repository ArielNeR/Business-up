package com.businessup

import android.app.Application
import com.businessup.data.database.AppDatabase
import com.businessup.data.repository.ClienteRepository
import com.businessup.data.repository.InventarioRepository
import com.businessup.data.repository.ProductoRepository
import com.businessup.data.repository.ServicioRepository
import com.businessup.data.repository.UsuarioRepository
import com.businessup.data.repository.VentaRepository

class BusinessUpApp : Application() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val usuarioRepository by lazy { UsuarioRepository(database.usuarioDao()) }
    val clienteRepository by lazy { ClienteRepository(database.clienteDao()) }
    val productoRepository by lazy { ProductoRepository(database.productoDao()) }
    val servicioRepository by lazy { ServicioRepository(database.servicioDao()) }
    val ventaRepository by lazy { VentaRepository(database.ventaDao()) }
    val inventarioRepository by lazy {
        InventarioRepository(database.productoDao(), database.servicioDao())
    }

    companion object {
        lateinit var instance: BusinessUpApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
