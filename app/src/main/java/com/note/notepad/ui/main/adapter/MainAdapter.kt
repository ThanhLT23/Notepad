package com.note.notepad.ui.main.adapter

import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.note.notepad.R
import com.note.notepad.data.local.model.relation.NoteWithCategories
import com.note.notepad.databinding.ItemNoteBinding
import com.note.notepad.utils.AppConstant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainAdapter(
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit,
    private val showCategory: Boolean = true
) : ListAdapter<NoteWithCategories, MainAdapter.MainViewHolder>(MainDiffCallback()) {

    inner class MainViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat(AppConstant.DATE_FORMAT, Locale.getDefault())
        fun bind(item: NoteWithCategories) {
            val note = item.note
            val categories = item.category
            val total = categories.size
            val LIMIT = 24
            var result = ""
            var addedCount = 0

            binding.tvNoteTitle.text = highlightText(note.title, currentSearchQuery)

            if (currentSearchQuery.isNotEmpty() && note.content.contains(
                    currentSearchQuery,
                    ignoreCase = true
                )
            ) {
                binding.tvNoteContent.visibility = View.VISIBLE
                val snippet = summarizeContent(note.content, currentSearchQuery)
                binding.tvNoteContent.text = highlightText(snippet, currentSearchQuery)
            } else {
                binding.tvNoteContent.isGone = true
            }

            val isCreationTime = currentSortOption == 4 || currentSortOption == 5
            val displayTime = if (isCreationTime) note.creationTime else note.lastTime
            val dateString = dateFormat.format(Date(displayTime))
            if (isCreationTime) {
                binding.tvLastEdit.text =
                    binding.root.context.getString(R.string.format_creation_time, dateString)
            } else {
                binding.tvLastEdit.text =
                    binding.root.context.getString(R.string.format_last_edit, dateString)
            }

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

            if (total == 0) {
                binding.tvNoteCategorize.isGone =true
            } else {
                binding.tvNoteCategorize.isVisible = true
            }

            for (i in 0 until total) {
                val name = categories[i].name
                val separator = if (result.isEmpty()) "" else ", "
                val potentialName = result + separator + name

                val remaining = total - (i + 1)
                val suffix = if (remaining > 0) " (+ $remaining" else ""

                if ((potentialName + suffix).length <= LIMIT) {
                    result = potentialName
                    addedCount++
                } else {
                    if (result.isEmpty()) {
                        result = name
                    } else {
                        result += " (+ ${total - addedCount})"
                    }
                    break
                }
            }
            binding.tvNoteCategorize.text = result
        }

        private fun highlightText(text: String, query: String): SpannableString {
            val spannable = SpannableString(text)
            if (query.isEmpty()) return spannable

            val lowerText = text.lowercase()
            val lowerQuery = query.lowercase()
            var startPos = lowerText.indexOf(lowerQuery)

            val color = ContextCompat.getColor(binding.root.context, R.color.highlightColor)
            while (startPos >= 0) {
                val endPos = startPos + query.length
                spannable.setSpan(
                    BackgroundColorSpan(color),
                    startPos,
                    endPos,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                startPos = lowerText.indexOf(lowerQuery, endPos)
            }
            return spannable
        }

        private fun summarizeContent(content: String, query: String): String {
            if (query.isEmpty() || content.isEmpty()) return content

            val lowerContent = content.lowercase()
            val lowerQuery = query.lowercase()
            val matchIndex = lowerContent.indexOf(lowerQuery)

            if (matchIndex == -1) return content
            val contextRadius = 11
            val startIndex = maxOf(0, matchIndex - contextRadius)
            val endIndex = minOf(content.length, matchIndex + query.length + contextRadius)
            var snippet = content.substring(startIndex, endIndex)
            if (startIndex == 0 || endIndex == content.length) {
                return "...${snippet.replace("\n", " ")}..."
            }
            if (startIndex > 0) snippet = "...$snippet"
            if (endIndex < content.length) snippet = "$snippet..."
            return snippet.replace("\n", " ")
        }
    }

    private val selectedIds = mutableSetOf<Int>()
    var isSelectionMode = false
    var currentSearchQuery: String = ""
    var currentSortOption = 0

    fun updateSelection(newSelectedIds: Set<Int>) {
        val oldSelected = selectedIds.toSet()
        selectedIds.clear()
        selectedIds.addAll(newSelectedIds)
        val changedIds = (oldSelected - newSelectedIds) + (newSelectedIds - oldSelected)
        currentList.forEachIndexed { index, item ->
            if (changedIds.contains(item.note.id)) {
                notifyItemChanged(index)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        if (currentSearchQuery != query) {
            currentSearchQuery = query
            notifyItemRangeChanged(0, currentList.size, AppConstant.UPDATE_HIGHLIGHT)
        }
    }

    fun updateSortOption(option: Int) {
        if (currentSortOption != option) {
            currentSortOption = option
            notifyItemRangeChanged(0, currentList.size, AppConstant.UPDATE_TIME_TEXT)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: MainViewHolder,
        position: Int,
        payloads: MutableList<Any?>
    ) {
        when {
            payloads.contains(AppConstant.UPDATE_HIGHLIGHT) -> {
                holder.bind(getItem(position))
            }

            payloads.contains(AppConstant.UPDATE_TIME_TEXT) -> {
                holder.bind(getItem(position))
            }

            else -> super.onBindViewHolder(holder, position, payloads)
        }
    }
}

class MainDiffCallback : DiffUtil.ItemCallback<NoteWithCategories>() {
    override fun areItemsTheSame(
        oldItem: NoteWithCategories,
        newItem: NoteWithCategories
    ): Boolean {
        return oldItem.note.id == newItem.note.id
    }

    override fun areContentsTheSame(
        oldItem: NoteWithCategories,
        newItem: NoteWithCategories
    ): Boolean {
        return oldItem == newItem
    }
}
