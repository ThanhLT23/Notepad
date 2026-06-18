package com.note.notepad.data.repository

import com.note.notepad.data.local.dao.NoteDao
import com.note.notepad.data.local.model.NoteItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NoteRepository(private val noteDao: NoteDao) {
    fun getAllNote(): Flow<List<NoteItems>> = noteDao.getAllNotes()
    suspend fun getNoteById(id: Int): NoteItems = withContext(Dispatchers.IO) {
        noteDao.getNoteById(id)
    }
    suspend fun insertNote(note: NoteItems): Long = withContext(Dispatchers.IO) {
        noteDao.insertNote(note)
    }
    suspend fun updateNote(note: NoteItems) = withContext(Dispatchers.IO) {
        noteDao.updateNote(note)
    }

    suspend fun softDelete(ids: List<Int>) = withContext(Dispatchers.IO) {
        noteDao.softDelete(ids)
    }

    fun getDeletedNote(): Flow<List<NoteItems>> = noteDao.getDeletedNotes()

    suspend fun restoreItem(ids: List<Int>) = withContext(Dispatchers.IO) {
        noteDao.restoreItem(ids)
    }
    suspend fun hardDelete(ids: List<Int>) = withContext(Dispatchers.IO) {
        noteDao.hardDelete(ids)
    }

    suspend fun clearTrash() = withContext(Dispatchers.IO) {
        noteDao.clearTrash()
    }

    suspend fun restoreAllTrash() = withContext(Dispatchers.IO) {
        noteDao.restoreAllTrash()
    }
}
