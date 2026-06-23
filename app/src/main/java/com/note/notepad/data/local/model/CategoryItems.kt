package com.note.notepad.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryItems(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val position: Int
)
