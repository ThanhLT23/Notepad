package com.note.notepad.common.helpers

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.note.notepad.R
import com.note.notepad.common.enums.NoteAction

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
        val message = "Delete the selected note?"
        MaterialAlertDialogBuilder(context)
            .setTitle("Delete")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                onDelete()
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


}