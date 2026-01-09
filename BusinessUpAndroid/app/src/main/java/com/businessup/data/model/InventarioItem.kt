package com.businessup.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Clase wrapper que representa un item del inventario.
 * Puede ser un Producto o un Servicio.
 */
@Parcelize
data class InventarioItem(
    val id: Long,
    val nombre: String,
    val tipo: TipoInventario,
    val precio: Double,
    val cantidad: Int = 0,
    val unidadMedida: String = "",
    val codigoBarra: String = ""
) : Parcelable {

    val datoPrecio: String
        get() = when (tipo) {
            TipoInventario.PRODUCTO -> "$${"%.2f".format(precio)} / $unidadMedida"
            TipoInventario.SERVICIO -> "$${"%.2f".format(precio)}"
        }

    val datoCantidad: String
        get() = when (tipo) {
            TipoInventario.PRODUCTO -> "$cantidad unidades"
            TipoInventario.SERVICIO -> ""
        }

    val tipoString: String
        get() = when (tipo) {
            TipoInventario.PRODUCTO -> "Producto"
            TipoInventario.SERVICIO -> "Servicio"
        }

    companion object {
        fun fromProducto(producto: Producto): InventarioItem {
            return InventarioItem(
                id = producto.id,
                nombre = producto.nombre,
                tipo = TipoInventario.PRODUCTO,
                precio = producto.precioVenta,
                cantidad = producto.cantidadTotal,
                unidadMedida = producto.unidadMedida,
                codigoBarra = producto.codigoBarra
            )
        }

        fun fromServicio(servicio: Servicio): InventarioItem {
            return InventarioItem(
                id = servicio.id,
                nombre = servicio.nombre,
                tipo = TipoInventario.SERVICIO,
                precio = servicio.valor,
                codigoBarra = servicio.codigoBarra
            )
        }
    }
}

enum class TipoInventario {
    PRODUCTO,
    SERVICIO
}
