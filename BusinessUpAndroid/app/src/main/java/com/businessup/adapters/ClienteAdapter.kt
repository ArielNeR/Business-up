package com.businessup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.businessup.data.model.Cliente
import com.businessup.databinding.ItemClienteBinding

class ClienteAdapter(
    private val onItemClick: (Cliente) -> Unit,
    private val onEdit: (Cliente) -> Unit,
    private val onDelete: (Cliente) -> Unit
) : ListAdapter<Cliente, ClienteAdapter.ViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemClienteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemClienteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(cliente: Cliente) {
            binding.tvClientName.text = cliente.nombre
            binding.tvClientId.text = "ID: ${cliente.idCliente}"
            binding.tvClientPhone.text = cliente.numerosContacto.firstOrNull() ?: ""

            binding.btnEdit.setOnClickListener {
                onEdit(cliente)
            }

            binding.btnDelete.setOnClickListener {
                onDelete(cliente)
            }
        }
    }

    class ClienteDiffCallback : DiffUtil.ItemCallback<Cliente>() {
        override fun areItemsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem == newItem
        }
    }
}
