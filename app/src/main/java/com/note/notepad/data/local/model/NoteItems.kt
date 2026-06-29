package com.note.notepad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteItems(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val lastTime: Long,
    val creationTime: Long,
    val color: Int = 0,
    val isSelected: Boolean = false,
    val isDeleted: Boolean = false
)
