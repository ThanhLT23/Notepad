package com.note.notepad.common.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.note.notepad.R
import com.note.notepad.common.enums.NoteAction
import com.note.notepad.common.extension.setTextOrGone
import com.note.notepad.data.local.model.CategoryItems

object DialogHelpers {
    fun undoAllDialog(
        context: Context,
        onUndoAll: () -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_note, null)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        tvTitle.text = context.getString(R.string.message_undo_all_dialog)
        btnOk.text = context.getString(R.string.option_undo_all)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            onUndoAll()
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("InflateParams")
    fun deleteDialog(
        context: Context,
        noteTitle: String,
        onDelete: () -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_note, null)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val displayTitle = noteTitle.ifBlank { context.getString(R.string.title_untitled) }
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        tvTitle.text = context.getString(R.string.delete_dialog_message, displayTitle)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            onDelete()
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("InflateParams")
    fun showItemAction(
        context: Context,
        noteTitle: String,
        noteContent: String,
        onAction: (NoteAction) -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_trash_action, null)
        val tvPreviewTitle = view.findViewById<TextView>(R.id.tvPreviewTitle)
        val tvPreviewContent = view.findViewById<TextView>(R.id.tvPreviewContent)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val rbUndelete = view.findViewById<RadioButton>(R.id.rbUndelete)

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        val isUntitled = noteTitle == context.getString(R.string.title_untitled)

        tvPreviewTitle.setTextOrGone(noteTitle, noteTitle.isNotBlank() && !isUntitled)
        tvPreviewContent.setTextOrGone(noteContent)
        tvTitle.text = context.getString(R.string.show_item_action_title)
        rbUndelete.isChecked = true
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnOk.setOnClickListener {
            val action = if (rbUndelete.isChecked) NoteAction.RESTORE else NoteAction.DELETE
            onAction(action)
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("InflateParams")
    fun clearTrashDialog(
        context: Context,
        onClear: () -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_note, null)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        tvTitle.text = context.getString(R.string.clear_trash_dialog_message)
        btnCancel.text = context.getString(R.string.option_no)
        btnOk.text = context.getString(R.string.option_yes)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            onClear()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun undeleteAllDialog(
        context: Context,
        onUndelete: () -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_note, null)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        tvTitle.text = context.getString(R.string.undelete_all_dialog_message)
        btnCancel.text = context.getString(R.string.option_no)
        btnOk.text = context.getString(R.string.option_yes)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            onUndelete()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun deleteSelectedDialog(
        context: Context,
        onDeleteSelection: () -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_note, null)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        tvTitle.text = context.getString(R.string.delete_selected_dialog_message)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            onDeleteSelection()
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("InflateParams")
    fun showConfirmDeleteDialog(
        context: Context,
        onDelete: () -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_note, null)
        val tvMessage = view.findViewById<TextView>(R.id.tvTitle)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        tvMessage.text = context.getString(R.string.show_confirm_delete_dialog_message)
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            onDelete()
            dialog.dismiss()
        }
        dialog.show()
    }

    @SuppressLint("DiscouragedApi")
    fun sortNotesDialog(
        context: Context,
        currentOption: Int,
        onSortAction: (Int) -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_sort_note, null)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val rgSort = view.findViewById<RadioGroup>(R.id.rgSort)

        val currentRbId =
            context.resources.getIdentifier("rb$currentOption", "id", context.packageName)
        if (currentRbId != 0) {
            rgSort.check(currentRbId)
        }

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            val checkedId = rgSort.checkedRadioButtonId
            if (checkedId != -1) {
                val viewIdName = context.resources.getResourceEntryName(checkedId)
                val selectedOption = viewIdName.replace("rb", "").toInt()
                onSortAction(selectedOption)
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showEditDialog(
        context: Context,
        currentName: String,
        onSave: (String) -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_category, null)
        val edtName = view.findViewById<EditText>(R.id.edtName)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)

        edtName.setText(currentName)
        edtName.setSelection(currentName.length)

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnOk.setOnClickListener {
            val newName = edtName.text.toString().trim()
            when {
                newName.isEmpty() -> {
                    tvError.text = context.getString(R.string.empty_error_message)
                    tvError.visibility = View.VISIBLE
                }

                newName == currentName -> {
                    tvError.text = context.getString(R.string.exists_name_error_message)
                    tvError.visibility = View.VISIBLE
                }

                else -> {
                    onSave(newName)
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    fun deleteCategoryDialog(
        context: Context,
        cateName: String,
        onDelete: () -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_note, null)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        tvTitle.text = context.getString(R.string.delete_category_message, cateName)
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            onDelete()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showCategorizeDialog(
        context: Context,
        allCategories: List<CategoryItems>,
        recentSelectedIds: List<Int> = emptyList(),
        onConfirm: (List<Int>) -> Unit
    ) {
        if (allCategories.isEmpty()) {
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_note, null)
            val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
            val btnCancel = view.findViewById<Button>(R.id.btnCancel)
            val btnOk = view.findViewById<Button>(R.id.btnOk)

            tvTitle.text = context.getString(R.string.no_categories_info)
            btnCancel.isGone = true

            val dialog = AlertDialog.Builder(context)
                .setView(view)
                .create()
            btnOk.setOnClickListener { dialog.dismiss() }
            dialog.show()
            return
        }
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_categorize_note, null)
        val rvCategories = view.findViewById<RecyclerView>(R.id.rvCategories)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val selectedIds = recentSelectedIds.toMutableList()

        rvCategories.layoutManager = LinearLayoutManager(context)
        rvCategories.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ) = object : RecyclerView.ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_category_option, parent, false)
            ) {}

            override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int
            ) {
                val category = allCategories[position]
                val tvCateName = holder.itemView.findViewById<TextView>(R.id.tvCateName)
                val cbCategory = holder.itemView.findViewById<CheckBox>(R.id.cbCategory)

                tvCateName.text = category.name
                cbCategory.setOnCheckedChangeListener(null)
                cbCategory.isChecked = category.id in selectedIds

                cbCategory.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        if (category.id !in selectedIds) selectedIds.add(category.id)
                    } else {
                        selectedIds.remove(category.id)
                    }
                }
                holder.itemView.setOnClickListener {
                    cbCategory.toggle()
                }
            }

            override fun getItemCount(): Int = allCategories.size
        }

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            onConfirm(selectedIds)
            dialog.dismiss()
        }
        dialog.show()

    }
    @SuppressLint("InflateParams")
    fun showColorDialog(
        context: Context,
        currentColor: Int,
        onSave: (Int) -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_color_picker, null)
        val rvColor = view.findViewById<RecyclerView>(R.id.rvColors)
        val btnRemoveColor = view.findViewById<Button>(R.id.btnRemoveColor)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)

        val colorList = listOf(
            ContextCompat.getColor(context, R.color.color_light_peach_pink),
            ContextCompat.getColor(context, R.color.color_peach_orange),
            ContextCompat.getColor(context, R.color.color_pastel_yellow),
            ContextCompat.getColor(context, R.color.color_pastel_green),
            ContextCompat.getColor(context, R.color.color_pastel_aqua),
            ContextCompat.getColor(context, R.color.color_soft_blue),
            ContextCompat.getColor(context, R.color.color_soft_purple),
            ContextCompat.getColor(context, R.color.color_pastel_pink),
            ContextCompat.getColor(context, R.color.color_off_white),
            ContextCompat.getColor(context, R.color.color_pastel_blue),
            ContextCompat.getColor(context, R.color.color_light_aqua),
            ContextCompat.getColor(context, R.color.color_cream_white),
            ContextCompat.getColor(context, R.color.color_vanilla),
            ContextCompat.getColor(context, R.color.color_soft_rose),
            ContextCompat.getColor(context, R.color.color_light_lavender),
            ContextCompat.getColor(context, R.color.color_muted_blue),
            ContextCompat.getColor(context, R.color.color_mist_blue),
            ContextCompat.getColor(context, R.color.color_mint_aqua),
            ContextCompat.getColor(context, R.color.color_mist_green),
            ContextCompat.getColor(context, R.color.color_dusty_rose),
            ContextCompat.getColor(context, R.color.color_plum_purple),
            ContextCompat.getColor(context, R.color.color_berry_pink),
            ContextCompat.getColor(context, R.color.color_coral_red),
            ContextCompat.getColor(context, R.color.color_coral_orange),
            ContextCompat.getColor(context, R.color.color_golden_yellow),
            ContextCompat.getColor(context, R.color.color_honey_cream)
        )
        var tempSelectedColor = currentColor
        updateTitleHighlight(tvTitle, tempSelectedColor)
        rvColor.layoutManager = GridLayoutManager(context, 6)
        val adapter = ColorGridAdapter(colorList, tempSelectedColor) { color ->
            tempSelectedColor = color
            updateTitleHighlight(tvTitle, tempSelectedColor)
        }
        rvColor.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        btnRemoveColor.setOnClickListener {
            tempSelectedColor = 0
            updateTitleHighlight(tvTitle, tempSelectedColor)
            adapter.updateSelectedColor(0)
        }
        btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        btnOk.setOnClickListener {
            onSave(tempSelectedColor)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateTitleHighlight(textView: TextView, color: Int) {
        val text = textView.text.toString()
        val spannable = SpannableString(text)
        if (color != 0) {
            spannable.setSpan(
                BackgroundColorSpan(color),
                0,
                text.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.text = spannable
    }

    private class ColorGridAdapter(
        private val colors: List<Int>,
        private var selectedColor: Int,
        private val onColorClicked: (Int) -> Unit
    ) : RecyclerView.Adapter<ColorGridAdapter.ColorViewHolder>() {
        inner class ColorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val viewColor: View = view.findViewById(R.id.viewColor)
            private val imgView: ImageView = view.findViewById(R.id.imgView)
            fun bind(color: Int) {
                if (color == 0) {
                    viewColor.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    viewColor.setBackgroundColor(color)
                }

                if (color == selectedColor) {
                    imgView.visibility = View.VISIBLE
                } else {
                    imgView.visibility = View.GONE
                }

                itemView.setOnClickListener {
                    val oldSelected = selectedColor
                    selectedColor = color

                    notifyItemChanged(colors.indexOf(oldSelected))
                    notifyItemChanged(adapterPosition)

                    onColorClicked(color)
                }
            }
        }

        fun updateSelectedColor(newColor: Int) {
            val oldColor = selectedColor
            selectedColor = newColor

            val oldIndex = colors.indexOf(oldColor)
            val newIndex = colors.indexOf(newColor)
            if (oldIndex != -1) {
                notifyItemChanged(oldIndex)
            }
            if (newIndex != -1) {
                notifyItemChanged(newIndex)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
            return ColorViewHolder(view)
        }

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
            holder.bind(colors[position])
        }

        override fun getItemCount() = colors.size
    }

}