package com.businessup.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val unidadMedida: String,
    val precioVenta: Double,
    val precioProveedor: Double,
    val cantidadTotal: Int,
    val codigoBarra: String = ""
) : Parcelable {

    val datoPrecio: String
        get() = "$${"%.2f".format(precioVenta)} / $unidadMedida"

    val datoCantidad: String
        get() = "$cantidadTotal unidades"
}
