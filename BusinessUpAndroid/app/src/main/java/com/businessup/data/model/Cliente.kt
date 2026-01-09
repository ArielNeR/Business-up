package com.businessup.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.businessup.data.database.Converters
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "clientes")
@TypeConverters(Converters::class)
data class Cliente(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val idCliente: String,
    val nombre: String,
    val numerosContacto: List<String> = emptyList(),
    val correos: List<String> = emptyList(),
    val cuentasBanco: List<CuentaBanco> = emptyList()
) : Parcelable

@Parcelize
data class CuentaBanco(
    val tipoCuenta: String,
    val codigoCuenta: String
) : Parcelable
