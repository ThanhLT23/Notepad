package com.note.notepad.common.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.note.notepad.R
import com.note.notepad.common.enums.NoteAction
import com.note.notepad.data.local.model.CategoryItems

object DialogHelpers {
    fun undoAllDialog(
        context: Context,
        onUndoAll: () -> Unit
    ) {
        val message = context.getString(R.string.message_undo_all_dialog)
        MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.option_undo_all)) { dialog, _ ->
                onUndoAll()
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.option_no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun deleteDialog(
        context: Context,
        noteTitle: String,
        onDelete: () -> Unit
    ) {
        val displayTitle = noteTitle.ifBlank { context.getString(R.string.title_untitled) }
        MaterialAlertDialogBuilder(context)
            .setMessage(context.getString(R.string.delete_dialog_message, displayTitle))
            .setPositiveButton(context.getString(R.string.option_delete)) { dialog, _ ->
                onDelete()
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.option_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showItemAction(
        context: Context,
        onAction: (NoteAction) -> Unit
    ) {
        val options = arrayOf(
            context.getString(R.string.option_undelete),
            context.getString(R.string.option_delete)
        )
        var selectedOption = 0
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.show_item_action_title))
            .setSingleChoiceItems(options, selectedOption) { _, option ->
                selectedOption = option
            }
            .setPositiveButton(context.getString(R.string.option_ok)) { dialog, _ ->
                val action = when (selectedOption) {
                    0 -> NoteAction.RESTORE
                    else -> NoteAction.DELETE
                }
                onAction(action)
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.option_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun clearTrashDialog(
        context: Context,
        onClear: () -> Unit
    ) {
        val message = context.getString(R.string.clear_trash_dialog_message)
        MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.option_yes)) { dialog, _ ->
                onClear()
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.option_no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun undeleteAllDialog(
        context: Context,
        onUndelete: () -> Unit
    ) {
        val message = context.getString(R.string.undelete_all_dialog_message)
        MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.option_yes)) { dialog, _ ->
                onUndelete()
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.option_no)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun deleteSelectedDialog(
        context: Context,
        onDeleteSelection: () -> Unit
    ) {
        val message = context.getString(R.string.delete_selected_dialog_message)
        MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.option_ok)) { dialog, _ ->
                onDeleteSelection()
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.option_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showConfirmDeleteDialog(
        context: Context,
        onDelete: () -> Unit
    ) {
        val message = context.getString(R.string.show_confirm_delete_dialog_message)
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.delete_title))
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.option_ok)) { dialog, _ ->
                onDelete()
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.option_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun sortNotesDialog(
        context: Context,
        currentOption: Int,
        onSortAction: (Int) -> Unit
    ) {
        val options = arrayOf(
            "edit date: from newest",
            "edit date: from oldest",
            "title: A to Z",
            "title: Z to A",
            "creation date: from newest",
            "creation date: from oldest"
        )
        var selectedOption = currentOption
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.sort_note_dialog_title))
            .setSingleChoiceItems(options, currentOption) { _, option ->
                selectedOption = option
            }
            .setPositiveButton(context.getString(R.string.sort_option)) { dialog, _ ->
                onSortAction(selectedOption)
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.option_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showEditDialog(
        context: Context,
        currentName: String,
        onSave: (String) -> Unit
    ) {
        val density = context.resources.displayMetrics.density

        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(
                (24 * density).toInt(),
                (8 * density).toInt(),
                (24 * density).toInt(),
                0
            )
        }

        val editText = EditText(context).apply {
            setText(currentName)
            setSelection(currentName.length)
            isSingleLine = true
            filters = arrayOf(InputFilter.LengthFilter(25))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val tvError = TextView(context).apply {
            setTextColor(context.getColor(R.color.red))
            textSize = 12f
            visibility = View.GONE
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = (4 * density).toInt()
            }
        }

        layout.addView(editText)
        layout.addView(tvError)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Edit category name")
            .setView(layout)
            .setPositiveButton(context.getString(R.string.option_ok), null)
            .setNegativeButton(context.getString(R.string.option_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.setOnShowListener {
            val btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnSave.setOnClickListener {
                val newName = editText.text.toString().trim()
                when {
                    newName.isEmpty() -> {
                        tvError.text = "Category name cannot be empty"
                        tvError.visibility = View.VISIBLE
                    }

                    newName == currentName -> {
                        tvError.text = "Category with that name already exists."
                        tvError.visibility = View.VISIBLE
                    }

                    else -> {
                        onSave(newName)
                        dialog.dismiss()
                    }
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
        MaterialAlertDialogBuilder(context)
            .setMessage("Delete category \'$cateName\'? Notes from the category won't be deleted.")
            .setPositiveButton(context.getString(R.string.option_ok)) { dialog, _ ->
                onDelete()
                dialog.dismiss()
            }
            .setNegativeButton(context.getString(R.string.option_cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showCategorizeDialog(
        context: Context,
        allCategories: List<CategoryItems>,
        recentSelectedIds: List<Int> = emptyList(),
        onConfirm: (List<Int>) -> Unit
    ) {
        val name = allCategories.map { it.name }.toTypedArray()
        val checkedItems = BooleanArray(allCategories.size) { index ->
            allCategories[index].id in recentSelectedIds
        }
        val selectedIds = recentSelectedIds.toMutableList()

        MaterialAlertDialogBuilder(context)
            .setTitle("Select category")
            .setMultiChoiceItems(name, checkedItems) { _, which, isChecked ->
                val catId = allCategories[which].id
                if (isChecked) selectedIds.add(catId) else selectedIds.remove(catId)
            }
            .setPositiveButton(context.getString(R.string.option_ok)) { _, _ ->
                onConfirm(selectedIds)
            }
            .setNegativeButton(context.getString(R.string.option_cancel), null)
            .show()
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
        rvColor.layoutManager = GridLayoutManager(context, 6)
        val adapter = ColorGridAdapter(colorList, tempSelectedColor) { color ->
            tempSelectedColor = color
        }
        rvColor.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()
        btnRemoveColor.setOnClickListener {
            onSave(0)
            dialog.dismiss()
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

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_color, parent, false)
            return ColorViewHolder(view)
        }

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
            holder.bind(colors[position])
        }

        override fun getItemCount() = colors.size
    }

}