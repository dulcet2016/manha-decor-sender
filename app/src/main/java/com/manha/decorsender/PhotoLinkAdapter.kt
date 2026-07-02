package com.manha.decorsender

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manha.decorsender.data.PhotoLink
import com.manha.decorsender.databinding.ItemPhotoLinkBinding

class PhotoLinkAdapter(
    private var items: List<PhotoLink>,
    private val onDelete: (PhotoLink) -> Unit
) : RecyclerView.Adapter<PhotoLinkAdapter.VH>() {

    inner class VH(val binding: ItemPhotoLinkBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemPhotoLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val link = items[position]
        holder.binding.tvLink.text = link.driveUrl
        holder.binding.btnDelete.setOnClickListener { onDelete(link) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<PhotoLink>) {
        items = newItems
        notifyDataSetChanged()
    }
}
