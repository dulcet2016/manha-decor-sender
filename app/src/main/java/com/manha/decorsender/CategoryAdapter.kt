package com.manha.decorsender

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.manha.decorsender.data.Category
import com.manha.decorsender.databinding.ItemCategoryBinding

class CategoryAdapter(
    private var items: List<Pair<Category, Int>>, // category + photo count
    private val onClick: (Category) -> Unit,
    private val onDelete: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VH>() {

    inner class VH(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val (category, count) = items[position]
        holder.binding.tvCategoryName.text = category.name
        holder.binding.tvPhotoCount.text = "$count photo(s)"
        holder.binding.root.setOnClickListener { onClick(category) }
        holder.binding.btnDelete.setOnClickListener { onDelete(category) }
    }

    override fun getItemCount() = items.size

    fun submitList(newItems: List<Pair<Category, Int>>) {
        items = newItems
        notifyDataSetChanged()
    }
}
