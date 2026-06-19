package com.note.notepad.common.helpers

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.note.notepad.common.enums.NoteAction
import com.note.notepad.data.local.model.NoteItems

object DialogHelpers {
    fun undoAllDialog(
        context: Context,
        onUndoAll:() -> Unit) {
        val message = "Remove all of the note changes made since the last opening of the note?"
        MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setPositiveButton("UNDO ALL") { dialog, _ ->
                onUndoAll()
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun deleteDialog(
        context: Context,
        noteTitle: String,
        onDelete: () -> Unit) {
        val displayTitle = noteTitle.ifBlank { "Untitled" }
        MaterialAlertDialogBuilder(context)
            .setMessage("The \'$displayTitle\' note will be deleted. Are you sure?")
            .setPositiveButton("DELETE") { dialog, _ ->
                onDelete()
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showItemAction(
        context: Context,
        onAction: (NoteAction) -> Unit) {
        val options = arrayOf("Undelete", "Delete")
        var selectedOption = 0
        MaterialAlertDialogBuilder(context)
            .setTitle("Select an action for the note:")
            .setSingleChoiceItems(options, selectedOption) { _, option ->
                selectedOption = option
            }
            .setPositiveButton("OK") { dialog, _ ->
                val action = when (selectedOption) {
                    0 -> NoteAction.RESTORE
                    else -> NoteAction.DELETE
                }
                onAction(action)
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}