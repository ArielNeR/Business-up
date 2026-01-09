package com.businessup.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.businessup.data.dao.ClienteDao
import com.businessup.data.dao.ProductoDao
import com.businessup.data.dao.ServicioDao
import com.businessup.data.dao.UsuarioDao
import com.businessup.data.dao.VentaDao
import com.businessup.data.model.Cliente
import com.businessup.data.model.Producto
import com.businessup.data.model.Servicio
import com.businessup.data.model.Usuario
import com.businessup.data.model.Venta

@Database(
    entities = [
        Usuario::class,
        Cliente::class,
        Producto::class,
        Servicio::class,
        Venta::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun clienteDao(): ClienteDao
    abstract fun productoDao(): ProductoDao
    abstract fun servicioDao(): ServicioDao
    abstract fun ventaDao(): VentaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "business_up_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
