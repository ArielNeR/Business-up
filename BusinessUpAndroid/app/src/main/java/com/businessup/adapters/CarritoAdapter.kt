package com.businessup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.businessup.data.model.CarritoItem
import com.businessup.databinding.ItemCarritoBinding

class CarritoAdapter(
    private val onQuantityChange: (CarritoItem, Int) -> Unit,
    private val onDelete: (CarritoItem) -> Unit
) : ListAdapter<CarritoItem, CarritoAdapter.ViewHolder>(CarritoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCarritoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemCarritoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CarritoItem) {
            binding.tvItemName.text = item.nombre
            binding.tvItemType.text = item.tipo.name
            binding.tvItemPrice.text = "$${String.format("%.2f", item.precio)}"
            binding.tvQuantity.text = item.cantidad.toString()
            binding.tvSubtotal.text = item.datoSubtotal

            binding.btnIncrease.setOnClickListener {
                onQuantityChange(item, item.cantidad + 1)
            }

            binding.btnDecrease.setOnClickListener {
                if (item.cantidad > 1) {
                    onQuantityChange(item, item.cantidad - 1)
                }
            }

            binding.btnDelete.setOnClickListener {
                onDelete(item)
            }
        }
    }

    class CarritoDiffCallback : DiffUtil.ItemCallback<CarritoItem>() {
        override fun areItemsTheSame(oldItem: CarritoItem, newItem: CarritoItem): Boolean {
            return oldItem.itemId == newItem.itemId && oldItem.tipo == newItem.tipo
        }

        override fun areContentsTheSame(oldItem: CarritoItem, newItem: CarritoItem): Boolean {
            return oldItem == newItem
        }
    }
}
