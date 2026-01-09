package com.businessup.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import java.security.MessageDigest
import java.text.NumberFormat
import java.util.Locale
import java.util.Random

// View Extensions
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

// Context Extensions
fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// Fragment Extensions
fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(message, duration)
}

// String Extensions
fun String.toMD5(): String {
    val md = MessageDigest.getInstance("MD5")
    val digest = md.digest(this.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}

fun String?.orEmpty(): String = this ?: ""

// Double Extensions
fun Double.toCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(this)
}

fun Double.formatDecimal(decimals: Int = 2): String {
    return "%.${decimals}f".format(this)
}

// Random ID Generator
fun generateRandomId(length: Int = 8): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val random = Random()
    return (1..length)
        .map { chars[random.nextInt(chars.length)] }
        .joinToString("")
}

// List Extensions
fun <T> List<T>.takeIfNotEmpty(): List<T>? = if (isNotEmpty()) this else null
