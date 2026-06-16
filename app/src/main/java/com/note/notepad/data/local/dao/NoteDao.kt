package com.note.notepad.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.note.notepad.data.local.model.NoteItems

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<NoteItems>

    @Insert
    suspend fun insertNote(note: NoteItems)

    @Update
    suspend fun updateNote(note: NoteItems)

    @Delete
    suspend fun deleteNote(note: NoteItems)
}