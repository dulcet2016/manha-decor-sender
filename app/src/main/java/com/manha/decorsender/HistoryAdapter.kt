package com.manha.decorsender

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manha.decorsender.data.SentHistory
import com.manha.decorsender.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(private var items: List<SentHistory>) : RecyclerView.Adapter<HistoryAdapter.VH>() {

    inner class VH(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    private val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        val nameText = if (item.clientName.isBlank()) "Client (no name)" else item.clientName
        holder.binding.tvName.text = "$nameText — ${item.categoryName}"
        val statusText = if (item.status == "SUCCESS") "${item.photoCount} photo(s) sent" else "Failed"
        holder.binding.tvDetails.text = "+91 ${item.phoneNumber.removePrefix("91")} • $statusText"
        holder.binding.tvTime.text = dateFormat.format(Date(item.sentAt))
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<SentHistory>) {
        items = newItems
        notifyDataSetChanged()
    }
}
