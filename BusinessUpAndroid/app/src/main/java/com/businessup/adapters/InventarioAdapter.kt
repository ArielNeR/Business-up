package com.businessup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.businessup.R
import com.businessup.data.model.InventarioItem
import com.businessup.data.model.TipoInventario
import com.businessup.databinding.ItemInventarioBinding

class InventarioAdapter(
    private val onItemClick: (InventarioItem) -> Unit,
    private val onEdit: (InventarioItem) -> Unit,
    private val onDelete: (InventarioItem) -> Unit
) : ListAdapter<InventarioItem, InventarioAdapter.ViewHolder>(InventarioDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventarioBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemInventarioBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(item: InventarioItem) {
            binding.tvItemName.text = item.nombre
            binding.tvItemType.text = item.tipoString
            binding.tvItemPrice.text = item.datoPrecio
            binding.tvItemQuantity.text = item.datoCantidad

            val iconRes = when (item.tipo) {
                TipoInventario.PRODUCTO -> R.drawable.ic_inventory
                TipoInventario.SERVICIO -> R.drawable.ic_receipt
            }
            binding.ivIcon.setImageResource(iconRes)

            binding.btnEdit.setOnClickListener {
                onEdit(item)
            }

            binding.btnDelete.setOnClickListener {
                onDelete(item)
            }
        }
    }

    class InventarioDiffCallback : DiffUtil.ItemCallback<InventarioItem>() {
        override fun areItemsTheSame(oldItem: InventarioItem, newItem: InventarioItem): Boolean {
            return oldItem.id == newItem.id && oldItem.tipo == newItem.tipo
        }

        override fun areContentsTheSame(oldItem: InventarioItem, newItem: InventarioItem): Boolean {
            return oldItem == newItem
        }
    }
}
