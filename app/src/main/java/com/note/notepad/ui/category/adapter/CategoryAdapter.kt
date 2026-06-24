package com.note.notepad.ui.category.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.note.notepad.data.local.model.CategoryItems
import com.note.notepad.databinding.ItemCategoryBinding

class CategoryAdapter(
    private val onEditClick: (CategoryItems) -> Unit,
    private val onDeleteClick: (CategoryItems) -> Unit
) : ListAdapter<CategoryItems, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    inner class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryItems) {
            binding.tvCateName.text = item.name
            binding.ivEditCate.setOnClickListener {
                onEditClick(item)
            }
            binding.ivDeleteCate.setOnClickListener {
                onDeleteClick(item)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryItems>() {
    override fun areItemsTheSame(oldItem: CategoryItems, newItem: CategoryItems) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: CategoryItems, newItem: CategoryItems) = oldItem == newItem
}