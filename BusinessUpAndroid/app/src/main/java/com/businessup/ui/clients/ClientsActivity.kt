package com.businessup.ui.clients

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.businessup.R
import com.businessup.adapters.ClienteAdapter
import com.businessup.data.model.Cliente
import com.businessup.databinding.ActivityClientsBinding
import com.businessup.utils.Resource
import com.businessup.utils.gone
import com.businessup.utils.toast
import com.businessup.utils.visible

class ClientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientsBinding
    private val viewModel: ClientesViewModel by viewModels()
    private lateinit var adapter: ClienteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupSearch()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ClienteAdapter(
            onItemClick = { /* Navigate to detail if needed */ },
            onEdit = { cliente ->
                val intent = Intent(this, AddClientActivity::class.java)
                intent.putExtra("cliente", cliente)
                startActivity(intent)
            },
            onDelete = { cliente ->
                showDeleteConfirmation(cliente)
            }
        )
        binding.rvClients.apply {
            layoutManager = LinearLayoutManager(this@ClientsActivity)
            adapter = this@ClientsActivity.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
        }
    }

    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddClientActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { clients ->
            adapter.submitList(clients)
            if (clients.isEmpty()) {
                binding.layoutEmpty.visible()
                binding.rvClients.gone()
            } else {
                binding.layoutEmpty.gone()
                binding.rvClients.visible()
            }
        }

        viewModel.deleteResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    toast("Cliente eliminado")
                }
                is Resource.Error -> {
                    toast(result.message ?: "Error al eliminar")
                }
                else -> {}
            }
        }
    }

    private fun showDeleteConfirmation(cliente: Cliente) {
        AlertDialog.Builder(this)
            .setTitle(R.string.btn_delete)
            .setMessage(R.string.dialog_confirm_delete)
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                viewModel.deleteCliente(cliente)
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setSearchQuery(binding.etSearch.text?.toString() ?: "")
    }
}
