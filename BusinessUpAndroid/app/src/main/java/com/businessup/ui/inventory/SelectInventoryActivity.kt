package com.businessup.ui.inventory

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.businessup.R
import com.businessup.adapters.InventarioAdapter
import com.businessup.data.model.InventarioItem
import com.businessup.databinding.ActivityInventoryBinding
import com.businessup.utils.gone
import com.businessup.utils.visible

class SelectInventoryActivity : AppCompatActivity() {

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
        observeViewModel()

        binding.fabAdd.gone()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Seleccionar Producto"
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = InventarioAdapter(
            onItemClick = { item ->
                showQuantityDialog(item)
            },
            onEdit = { },
            onDelete = { }
        )
        binding.rvInventory.apply {
            layoutManager = LinearLayoutManager(this@SelectInventoryActivity)
            adapter = this@SelectInventoryActivity.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.setSearchQuery(text?.toString() ?: "")
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
    }

    private fun showQuantityDialog(item: InventarioItem) {
        val dialogView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null)
        val editText = EditText(this).apply {
            hint = "Cantidad"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText("1")
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_select_quantity)
            .setView(editText)
            .setPositiveButton(R.string.btn_accept) { _, _ ->
                val quantity = editText.text.toString().toIntOrNull() ?: 1
                val resultIntent = Intent()
                resultIntent.putExtra("item", item)
                resultIntent.putExtra("quantity", quantity)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }
}
