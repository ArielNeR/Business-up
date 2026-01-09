package com.businessup.ui.balance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.businessup.BusinessUpApp
import com.businessup.data.model.Venta
import kotlinx.coroutines.launch
import java.util.Calendar

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as BusinessUpApp
    private val ventaRepository = app.ventaRepository
    private val productoRepository = app.productoRepository

    // Cuentas por cobrar (unpaid sales)
    private val _totalCuentasPorCobrar = MutableLiveData<Double>()
    val totalCuentasPorCobrar: LiveData<Double> = _totalCuentasPorCobrar

    private val _countCuentasPorCobrar = MutableLiveData<Int>()
    val countCuentasPorCobrar: LiveData<Int> = _countCuentasPorCobrar

    // Ventas realizadas (paid sales)
    private val _totalVentasRealizadas = MutableLiveData<Double>()
    val totalVentasRealizadas: LiveData<Double> = _totalVentasRealizadas

    private val _countVentasRealizadas = MutableLiveData<Int>()
    val countVentasRealizadas: LiveData<Int> = _countVentasRealizadas

    // Inventory value
    private val _valorInventario = MutableLiveData<Double>()
    val valorInventario: LiveData<Double> = _valorInventario

    // Daily earnings
    private val _gananciaDiaria = MutableLiveData<Double>()
    val gananciaDiaria: LiveData<Double> = _gananciaDiaria

    // Chart data
    private val _ventasChartData = MutableLiveData<List<Pair<String, Float>>>()
    val ventasChartData: LiveData<List<Pair<String, Float>>> = _ventasChartData

    private val _gananciaChartData = MutableLiveData<List<Pair<String, Float>>>()
    val gananciaChartData: LiveData<List<Pair<String, Float>>> = _gananciaChartData

    // Selected date
    private val _selectedDate = MutableLiveData<Long>()
    val selectedDate: LiveData<Long> = _selectedDate

    // Ventas del día seleccionado
    private val _ventasDelDia = MutableLiveData<List<Venta>>()
    val ventasDelDia: LiveData<List<Venta>> = _ventasDelDia

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        _selectedDate.value = System.currentTimeMillis()
        loadData()
    }

    fun setDate(dateMillis: Long) {
        _selectedDate.value = dateMillis
        viewModelScope.launch {
            loadDailyData(dateMillis)
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                // Load cuentas por cobrar
                val ventasNoPagadas = ventaRepository.getVentasNoPagadas()
                _totalCuentasPorCobrar.value = ventasNoPagadas.sumOf { it.total }
                _countCuentasPorCobrar.value = ventasNoPagadas.size

                // Load ventas realizadas
                val ventasPagadas = ventaRepository.getVentasPagadas()
                _totalVentasRealizadas.value = ventasPagadas.sumOf { it.total }
                _countVentasRealizadas.value = ventasPagadas.size

                // Load inventory value
                _valorInventario.value = productoRepository.getValorTotalInventario()

                // Load daily data
                loadDailyData(_selectedDate.value ?: System.currentTimeMillis())

                // Load chart data (last 7 days)
                loadChartData()

            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadDailyData(dateMillis: Long) {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dateMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val endOfDay = calendar.timeInMillis - 1

        val ventasPagadasDelDia = ventaRepository.getVentasPagadasByFecha(startOfDay, endOfDay)
        _gananciaDiaria.value = ventasPagadasDelDia.sumOf { it.total }
        _ventasDelDia.value = ventasPagadasDelDia
    }

    private suspend fun loadChartData() {
        val chartEntries = mutableListOf<Pair<String, Float>>()
        val gananciaEntries = mutableListOf<Pair<String, Float>>()

        val calendar = Calendar.getInstance()

        for (i in 6 downTo 0) {
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_MONTH, -i)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val startOfDay = calendar.timeInMillis
            calendar.add(Calendar.DAY_OF_MONTH, 1)
            val endOfDay = calendar.timeInMillis - 1

            val ventasDelDia = ventaRepository.getVentasPagadasByFecha(startOfDay, endOfDay)
            val totalDelDia = ventasDelDia.sumOf { it.total }

            val dayLabel = "${calendar.get(Calendar.DAY_OF_MONTH)}/${calendar.get(Calendar.MONTH) + 1}"

            chartEntries.add(Pair(dayLabel, ventasDelDia.size.toFloat()))
            gananciaEntries.add(Pair(dayLabel, totalDelDia.toFloat()))
        }

        _ventasChartData.value = chartEntries
        _gananciaChartData.value = gananciaEntries
    }

    fun refresh() {
        loadData()
    }
}
