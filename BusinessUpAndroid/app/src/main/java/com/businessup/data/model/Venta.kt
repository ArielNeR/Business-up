package com.businessup.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.businessup.data.database.Converters
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
@Entity(tableName = "ventas")
@TypeConverters(Converters::class)
data class Venta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val numeroFactura: Int,
    val clienteId: Long,
    val clienteNombre: String,
    val productos: List<CarritoItem> = emptyList(),
    val pagado: Boolean = false,
    val fecha: Long = System.currentTimeMillis(),
    val metodoPago: String = "Efectivo",
    val facturaPdf: ByteArray? = null
) : Parcelable {

    val total: Double
        get() = productos.sumOf { it.precio * it.cantidad }

    val datoTotal: String
        get() = "$${"%.2f".format(total)}"

    val datoFecha: String
        get() {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            return dateFormat.format(Date(fecha))
        }

    val datoFechaCompleta: String
        get() {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(Date(fecha))
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Venta

        if (id != other.id) return false
        if (numeroFactura != other.numeroFactura) return false
        if (clienteId != other.clienteId) return false
        if (clienteNombre != other.clienteNombre) return false
        if (productos != other.productos) return false
        if (pagado != other.pagado) return false
        if (fecha != other.fecha) return false
        if (metodoPago != other.metodoPago) return false
        if (facturaPdf != null) {
            if (other.facturaPdf == null) return false
            if (!facturaPdf.contentEquals(other.facturaPdf)) return false
        } else if (other.facturaPdf != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + numeroFactura
        result = 31 * result + clienteId.hashCode()
        result = 31 * result + clienteNombre.hashCode()
        result = 31 * result + productos.hashCode()
        result = 31 * result + pagado.hashCode()
        result = 31 * result + fecha.hashCode()
        result = 31 * result + metodoPago.hashCode()
        result = 31 * result + (facturaPdf?.contentHashCode() ?: 0)
        return result
    }
}

@Parcelize
data class CarritoItem(
    val itemId: Long,
    val nombre: String,
    val tipo: TipoInventario,
    val precio: Double,
    var cantidad: Int,
    val unidadMedida: String = ""
) : Parcelable {

    val subtotal: Double
        get() = precio * cantidad

    val datoSubtotal: String
        get() = "$${"%.2f".format(subtotal)}"

    val datoCantidad: String
        get() = when (tipo) {
            TipoInventario.PRODUCTO -> "$cantidad unidades"
            TipoInventario.SERVICIO -> "$cantidad veces"
        }
}
