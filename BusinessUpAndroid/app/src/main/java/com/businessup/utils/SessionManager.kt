package com.businessup.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.businessup.data.model.Usuario
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "business_up_session",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val gson = Gson()

    companion object {
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_DATA = "user_data"

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    fun saveSession(usuario: Usuario) {
        sharedPreferences.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putLong(KEY_USER_ID, usuario.id)
            putString(KEY_USER_DATA, gson.toJson(usuario))
            apply()
        }
    }

    fun updateUserData(usuario: Usuario) {
        sharedPreferences.edit().apply {
            putString(KEY_USER_DATA, gson.toJson(usuario))
            apply()
        }
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong(KEY_USER_ID, -1)
    }

    fun getUser(): Usuario? {
        val userData = sharedPreferences.getString(KEY_USER_DATA, null)
        return userData?.let {
            try {
                gson.fromJson(it, Usuario::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}
