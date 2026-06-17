package com.note.notepad.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.note.notepad.R
import com.note.notepad.data.local.model.NoteItems
import com.note.notepad.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit
) : ListAdapter<NoteItems, MainAdapter.MainViewHolder>(MainDiffCallback()) {

    inner class MainViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("dd/M/yy, HH:mm", Locale.getDefault())
        fun bind(note: NoteItems) {
            binding.tvNoteTitle.text = note.title
            val dateString = dateFormat.format(Date(note.lastTime))
            binding.tvLastEdit.text =
                binding.root.context.getString(R.string.format_last_edit, dateString)
            val isSelected = selectedIds.contains(note.id)
            if (isSelected) {
                binding.root.setBackgroundResource(R.drawable.bg_note_selected)
            } else {
                binding.root.setBackgroundResource(R.drawable.bg_note_item)
            }

            binding.root.setOnClickListener {
                if (isSelectionMode) {
                    onItemLongClick(note.id)
                } else {
                    onItemClick(note.id)
                }
            }

            binding.root.setOnLongClickListener {
                if (!isSelectionMode) {
                    onItemLongClick(note.id)
                }
                true
            }

        }
    }

    private val selectedIds = mutableSetOf<Int>()

    var isSelectionMode = false

    fun updateSelection(newSelectedIds: Set<Int>) {
        val oldSelected = selectedIds.toSet()
        selectedIds.clear()
        selectedIds.addAll(newSelectedIds)
        val changedIds = (oldSelected - newSelectedIds) + (newSelectedIds - oldSelected)
        currentList.forEachIndexed { index, noteItems ->
            if (changedIds.contains(noteItems.id)) {
                notifyItemChanged(index)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MainDiffCallback : DiffUtil.ItemCallback<NoteItems>() {
    override fun areItemsTheSame(oldItem: NoteItems, newItem: NoteItems): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: NoteItems, newItem: NoteItems): Boolean {
        return oldItem == newItem
    }
}
