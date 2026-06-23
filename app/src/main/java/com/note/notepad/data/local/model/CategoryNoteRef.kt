package com.note.notepad.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "category_note_ref",
    primaryKeys = ["noteId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = NoteItems::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryItems::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class CategoryNoteRef(
    val noteId: Int,
    val categoryId: Int
)
