package com.businessup.ui.balance

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.businessup.R
import com.businessup.databinding.FragmentBalanceBinding
import com.businessup.ui.inventory.InventoryActivity
import com.businessup.ui.sales.SalesListActivity
import com.businessup.ui.sales.SalesListType
import com.businessup.utils.toCurrency
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BalanceFragment : Fragment() {

    private var _binding: FragmentBalanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BalanceViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBalanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCharts()
        setupClickListeners()
        setupSwipeRefresh()
        observeViewModel()
    }

    private fun setupCharts() {
        // Setup sales chart
        binding.chartVentas.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.WHITE
                setDrawGridLines(false)
            }
            axisLeft.apply {
                textColor = Color.WHITE
                setDrawGridLines(false)
            }
            axisRight.isEnabled = false
        }

        // Setup earnings chart
        binding.chartGanancia.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.WHITE
                setDrawGridLines(false)
            }
            axisLeft.apply {
                textColor = Color.WHITE
                setDrawGridLines(false)
            }
            axisRight.isEnabled = false
        }
    }

    private fun setupClickListeners() {
        binding.cardDatePicker.setOnClickListener {
            showDatePicker()
        }

        binding.cardCuentasPorCobrar.setOnClickListener {
            val intent = Intent(requireContext(), SalesListActivity::class.java)
            intent.putExtra("type", SalesListType.UNPAID.name)
            startActivity(intent)
        }

        binding.cardVentasRealizadas.setOnClickListener {
            val intent = Intent(requireContext(), SalesListActivity::class.java)
            intent.putExtra("type", SalesListType.PAID.name)
            startActivity(intent)
        }

        binding.cardValorInventario.setOnClickListener {
            startActivity(Intent(requireContext(), InventoryActivity::class.java))
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(R.color.primary)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        viewModel.selectedDate.value?.let {
            calendar.timeInMillis = it
        }

        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                viewModel.setDate(calendar.timeInMillis)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun observeViewModel() {
        viewModel.selectedDate.observe(viewLifecycleOwner) { dateMillis ->
            binding.tvSelectedDate.text = dateFormat.format(Date(dateMillis))
        }

        viewModel.totalCuentasPorCobrar.observe(viewLifecycleOwner) { total ->
            binding.tvTotalCuentasPorCobrar.text = total.toCurrency()
        }

        viewModel.countCuentasPorCobrar.observe(viewLifecycleOwner) { count ->
            binding.tvCountCuentasPorCobrar.text = "$count ventas pendientes"
        }

        viewModel.totalVentasRealizadas.observe(viewLifecycleOwner) { total ->
            binding.tvTotalVentasRealizadas.text = total.toCurrency()
        }

        viewModel.countVentasRealizadas.observe(viewLifecycleOwner) { count ->
            binding.tvCountVentasRealizadas.text = "$count ventas realizadas"
        }

        viewModel.valorInventario.observe(viewLifecycleOwner) { valor ->
            binding.tvValorInventario.text = valor.toCurrency()
        }

        viewModel.gananciaDiaria.observe(viewLifecycleOwner) { ganancia ->
            binding.tvGananciaDiaria.text = ganancia.toCurrency()
        }

        viewModel.ventasChartData.observe(viewLifecycleOwner) { data ->
            updateVentasChart(data)
        }

        viewModel.gananciaChartData.observe(viewLifecycleOwner) { data ->
            updateGananciaChart(data)
        }
    }

    private fun updateVentasChart(data: List<Pair<String, Float>>) {
        val entries = data.mapIndexed { index, pair ->
            Entry(index.toFloat(), pair.second)
        }

        val dataSet = LineDataSet(entries, "Ventas").apply {
            color = Color.parseColor("#FF1943")
            lineWidth = 2f
            setCircleColor(Color.WHITE)
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        binding.chartVentas.apply {
            xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.first })
            this.data = LineData(dataSet)
            invalidate()
        }
    }

    private fun updateGananciaChart(data: List<Pair<String, Float>>) {
        val entries = data.mapIndexed { index, pair ->
            Entry(index.toFloat(), pair.second)
        }

        val dataSet = LineDataSet(entries, "Ganancia").apply {
            color = Color.parseColor("#FF1943")
            lineWidth = 2f
            setCircleColor(Color.WHITE)
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        binding.chartGanancia.apply {
            xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.first })
            this.data = LineData(dataSet)
            invalidate()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
