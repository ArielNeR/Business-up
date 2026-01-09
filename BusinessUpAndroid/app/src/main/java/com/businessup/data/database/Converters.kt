package com.businessup.data.database

import androidx.room.TypeConverter
import com.businessup.data.model.CarritoItem
import com.businessup.data.model.CuentaBanco
import com.businessup.data.model.TipoInventario
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    // String List converters
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // CuentaBanco List converters
    @TypeConverter
    fun fromCuentaBancoList(value: List<CuentaBanco>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCuentaBancoList(value: String): List<CuentaBanco> {
        val listType = object : TypeToken<List<CuentaBanco>>() {}.type
        return try {
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // CarritoItem List converters
    @TypeConverter
    fun fromCarritoItemList(value: List<CarritoItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toCarritoItemList(value: String): List<CarritoItem> {
        val listType = object : TypeToken<List<CarritoItem>>() {}.type
        return try {
            gson.fromJson(value, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // TipoInventario converter
    @TypeConverter
    fun fromTipoInventario(value: TipoInventario): String {
        return value.name
    }

    @TypeConverter
    fun toTipoInventario(value: String): TipoInventario {
        return try {
            TipoInventario.valueOf(value)
        } catch (e: Exception) {
            TipoInventario.PRODUCTO
        }
    }

    // ByteArray converter
    @TypeConverter
    fun fromByteArray(value: ByteArray?): String? {
        return value?.let { android.util.Base64.encodeToString(it, android.util.Base64.DEFAULT) }
    }

    @TypeConverter
    fun toByteArray(value: String?): ByteArray? {
        return value?.let { android.util.Base64.decode(it, android.util.Base64.DEFAULT) }
    }
}
