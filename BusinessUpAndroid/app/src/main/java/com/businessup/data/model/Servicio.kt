package com.businessup.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "servicios")
data class Servicio(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String,
    val valor: Double,
    val codigoBarra: String = ""
) : Parcelable {

    val datoValor: String
        get() = "$${"%.2f".format(valor)}"
}
