package com.businessup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.businessup.R
import com.businessup.data.model.Venta
import com.businessup.databinding.ItemVentaBinding
import com.businessup.utils.gone
import com.businessup.utils.visible

class VentaListAdapter(
    private val onViewInvoice: (Venta) -> Unit,
    private val onMarkPaid: (Venta) -> Unit
) : ListAdapter<Venta, VentaListAdapter.ViewHolder>(VentaDiffCallback()) {

    private var showMarkPaidButton = false

    fun setShowMarkPaidButton(show: Boolean) {
        showMarkPaidButton = show
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemVentaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemVentaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(venta: Venta) {
            binding.tvNumeroFactura.text = "Factura #${venta.numeroFactura}"
            binding.tvClienteName.text = venta.clienteNombre
            binding.tvTotal.text = venta.datoTotal
            binding.tvFecha.text = venta.datoFechaCompleta

            // Status
            if (venta.pagado) {
                binding.tvEstado.text = "Pagada"
                binding.tvEstado.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.success)
                )
            } else {
                binding.tvEstado.text = "Pendiente"
                binding.tvEstado.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.warning)
                )
            }

            // View invoice button
            binding.btnViewInvoice.isEnabled = venta.facturaPdf != null
            binding.btnViewInvoice.setOnClickListener {
                onViewInvoice(venta)
            }

            // Mark paid button
            if (showMarkPaidButton && !venta.pagado) {
                binding.btnMarkPaid.visible()
                binding.btnMarkPaid.setOnClickListener {
                    onMarkPaid(venta)
                }
            } else {
                binding.btnMarkPaid.gone()
            }
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
