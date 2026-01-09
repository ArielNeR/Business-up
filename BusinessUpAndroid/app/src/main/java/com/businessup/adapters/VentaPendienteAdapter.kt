package com.businessup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.businessup.data.model.Venta
import com.businessup.databinding.ItemVentaPendienteBinding

class VentaPendienteAdapter(
    private val onViewInvoice: (Venta) -> Unit
) : ListAdapter<Venta, VentaPendienteAdapter.ViewHolder>(VentaDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVentaPendienteBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemVentaPendienteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(venta: Venta) {
            binding.tvClienteName.text = venta.clienteNombre
            binding.tvTotal.text = venta.datoTotal
            binding.tvFecha.text = venta.datoFecha

            binding.btnViewInvoice.setOnClickListener {
                onViewInvoice(venta)
            }

            // Enable/disable button based on PDF availability
            binding.btnViewInvoice.isEnabled = venta.facturaPdf != null
        }
    }

    class VentaDiffCallback : DiffUtil.ItemCallback<Venta>() {
        override fun areItemsTheSame(oldItem: Venta, newItem: Venta): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Venta, newItem: Venta): Boolean {
            return oldItem == newItem
        }
    }
}
