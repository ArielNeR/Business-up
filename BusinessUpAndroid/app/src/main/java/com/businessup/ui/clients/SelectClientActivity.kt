package com.businessup.ui.clients

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.businessup.adapters.ClienteAdapter
import com.businessup.databinding.ActivityClientsBinding
import com.businessup.utils.gone
import com.businessup.utils.visible

class SelectClientActivity : AppCompatActivity() {

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
        observeViewModel()

        // Hide FAB in selection mode
        binding.fabAdd.gone()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Seleccionar Cliente"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = ClienteAdapter(
            onItemClick = { cliente ->
                val resultIntent = Intent()
                resultIntent.putExtra("cliente", cliente)
                setResult(RESULT_OK, resultIntent)
                finish()
            },
            onEdit = { },
            onDelete = { }
        )

        // Hide edit/delete buttons in selection mode
        binding.rvClients.apply {
            layoutManager = LinearLayoutManager(this@SelectClientActivity)
            adapter = this@SelectClientActivity.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
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
    }
}
