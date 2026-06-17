package com.note.notepad.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.note.notepad.data.local.model.NoteItems
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0")
    fun getAllNotes(): Flow<List<NoteItems>>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: Int): NoteItems

    @Insert
    suspend fun insertNote(note: NoteItems) : Long

    @Update
    suspend fun updateNote(note: NoteItems)

    @Query("UPDATE notes SET isDeleted = 1, isSelected = 0 WHERE id IN (:ids)")
    suspend fun softDelete(ids: List<Int>)

}