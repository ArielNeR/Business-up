package com.businessup.ui.inventory

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.businessup.R
import com.businessup.adapters.InventarioAdapter
import com.businessup.data.model.InventarioItem
import com.businessup.data.model.TipoInventario
import com.businessup.databinding.ActivityInventoryBinding
import com.businessup.utils.Resource
import com.businessup.utils.gone
import com.businessup.utils.toast
import com.businessup.utils.visible

class InventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private val viewModel: InventarioViewModel by viewModels()
    private lateinit var adapter: InventarioAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
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
        adapter = InventarioAdapter(
            onItemClick = { },
            onEdit = { item ->
                when (item.tipo) {
                    TipoInventario.PRODUCTO -> {
                        val intent = Intent(this, AddProductActivity::class.java)
                        intent.putExtra("item", item)
                        startActivity(intent)
                    }
                    TipoInventario.SERVICIO -> {
                        val intent = Intent(this, AddServiceActivity::class.java)
                        intent.putExtra("item", item)
                        startActivity(intent)
                    }
                }
            },
            onDelete = { item ->
                showDeleteConfirmation(item)
            }
        )
        binding.rvInventory.apply {
            layoutManager = LinearLayoutManager(this@InventoryActivity)
            adapter = this@InventoryActivity.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
        }
    }

    private fun setupClickListeners() {
        binding.fabAdd.setOnClickListener {
            showAddOptionsDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(this) { items ->
            adapter.submitList(items)
            if (items.isEmpty()) {
                binding.layoutEmpty.visible()
                binding.rvInventory.gone()
            } else {
                binding.layoutEmpty.gone()
                binding.rvInventory.visible()
            }
        }

        viewModel.deleteResult.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    toast("Elemento eliminado")
                }
                is Resource.Error -> {
                    toast(result.message ?: "Error al eliminar")
                }
                else -> {}
            }
        }
    }

    private fun showAddOptionsDialog() {
        val options = arrayOf(
            getString(R.string.inventory_add_product),
            getString(R.string.inventory_add_service)
        )

        AlertDialog.Builder(this)
            .setTitle(R.string.btn_add)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> startActivity(Intent(this, AddProductActivity::class.java))
                    1 -> startActivity(Intent(this, AddServiceActivity::class.java))
                }
            }
            .show()
    }

    private fun showDeleteConfirmation(item: InventarioItem) {
        AlertDialog.Builder(this)
            .setTitle(R.string.btn_delete)
            .setMessage(R.string.dialog_confirm_delete)
            .setPositiveButton(R.string.btn_delete) { _, _ ->
                viewModel.deleteItem(item)
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.setSearchQuery(binding.etSearch.text?.toString() ?: "")
    }
}
