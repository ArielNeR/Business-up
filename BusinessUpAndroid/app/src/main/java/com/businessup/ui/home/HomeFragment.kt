package com.businessup.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.businessup.data.model.Cliente
import com.businessup.data.model.InventarioItem
import com.businessup.data.model.Venta
import com.businessup.databinding.FragmentHomeBinding
import com.businessup.ui.clients.AddClientActivity
import com.businessup.ui.pdf.PdfViewerActivity
import com.businessup.ui.sales.SaleActivity
import com.businessup.adapters.ClienteHorizontalAdapter
import com.businessup.adapters.InventarioHorizontalAdapter
import com.businessup.adapters.VentaPendienteAdapter
import com.businessup.utils.UiState
import com.businessup.utils.gone
import com.businessup.utils.visible

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var clienteAdapter: ClienteHorizontalAdapter
    private lateinit var inventarioAdapter: InventarioHorizontalAdapter
    private lateinit var ventaAdapter: VentaPendienteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSwipeRefresh()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        // Clients horizontal RecyclerView
        clienteAdapter = ClienteHorizontalAdapter { cliente ->
            // Navigate to Sale with selected client
            val intent = Intent(requireContext(), SaleActivity::class.java)
            intent.putExtra("cliente", cliente)
            startActivity(intent)
        }
        binding.rvClientes.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = clienteAdapter
        }

        // Inventory horizontal RecyclerView
        inventarioAdapter = InventarioHorizontalAdapter { item ->
            // Navigate to inventory detail
        }
        binding.rvInventario.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = inventarioAdapter
        }

        // Pending sales RecyclerView
        ventaAdapter = VentaPendienteAdapter(
            onViewInvoice = { venta ->
                venta.facturaPdf?.let { pdfBytes ->
                    val intent = Intent(requireContext(), PdfViewerActivity::class.java)
                    intent.putExtra("pdf_bytes", pdfBytes)
                    intent.putExtra("title", "Factura #${venta.numeroFactura}")
                    startActivity(intent)
                }
            }
        )
        binding.rvVentasPendientes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ventaAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeResources(
            com.businessup.R.color.primary
        )
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
    }

    private fun setupClickListeners() {
        // FAB for new sale
        binding.fabNewSale.setOnClickListener {
            startActivity(Intent(requireContext(), SaleActivity::class.java))
        }

        // Quick action cards
        binding.cardNewSale.setOnClickListener {
            startActivity(Intent(requireContext(), SaleActivity::class.java))
        }

        binding.cardAddClient.setOnClickListener {
            startActivity(Intent(requireContext(), AddClientActivity::class.java))
        }
    }

    private fun observeViewModel() {
        // Observe clients state
        viewModel.clientesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.shimmerClientes.visible()
                    binding.shimmerClientes.startShimmer()
                    binding.rvClientes.gone()
                    binding.tvEmptyClientes.gone()
                }
                is UiState.Success -> {
                    binding.shimmerClientes.stopShimmer()
                    binding.shimmerClientes.gone()
                    binding.rvClientes.visible()
                    binding.tvEmptyClientes.gone()
                    clienteAdapter.submitList(state.data)
                }
                is UiState.Empty -> {
                    binding.shimmerClientes.stopShimmer()
                    binding.shimmerClientes.gone()
                    binding.rvClientes.gone()
                    binding.tvEmptyClientes.visible()
                }
                is UiState.Error -> {
                    binding.shimmerClientes.stopShimmer()
                    binding.shimmerClientes.gone()
                    binding.rvClientes.gone()
                    binding.tvEmptyClientes.visible()
                }
            }
        }

        // Observe inventory state
        viewModel.inventarioState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.shimmerInventario.visible()
                    binding.shimmerInventario.startShimmer()
                    binding.rvInventario.gone()
                    binding.tvEmptyInventario.gone()
                }
                is UiState.Success -> {
                    binding.shimmerInventario.stopShimmer()
                    binding.shimmerInventario.gone()
                    binding.rvInventario.visible()
                    binding.tvEmptyInventario.gone()
                    inventarioAdapter.submitList(state.data)
                }
                is UiState.Empty -> {
                    binding.shimmerInventario.stopShimmer()
                    binding.shimmerInventario.gone()
                    binding.rvInventario.gone()
                    binding.tvEmptyInventario.visible()
                }
                is UiState.Error -> {
                    binding.shimmerInventario.stopShimmer()
                    binding.shimmerInventario.gone()
                    binding.rvInventario.gone()
                    binding.tvEmptyInventario.visible()
                }
            }
        }

        // Observe pending sales state
        viewModel.ventasPendientesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.shimmerVentas.visible()
                    binding.shimmerVentas.startShimmer()
                    binding.rvVentasPendientes.gone()
                    binding.tvEmptyVentas.gone()
                }
                is UiState.Success -> {
                    binding.shimmerVentas.stopShimmer()
                    binding.shimmerVentas.gone()
                    binding.rvVentasPendientes.visible()
                    binding.tvEmptyVentas.gone()
                    ventaAdapter.submitList(state.data)
                }
                is UiState.Empty -> {
                    binding.shimmerVentas.stopShimmer()
                    binding.shimmerVentas.gone()
                    binding.rvVentasPendientes.gone()
                    binding.tvEmptyVentas.visible()
                }
                is UiState.Error -> {
                    binding.shimmerVentas.stopShimmer()
                    binding.shimmerVentas.gone()
                    binding.rvVentasPendientes.gone()
                    binding.tvEmptyVentas.visible()
                }
            }
        }

        // Observe refresh state
        viewModel.isRefreshing.observe(viewLifecycleOwner) { isRefreshing ->
            binding.swipeRefresh.isRefreshing = isRefreshing
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
