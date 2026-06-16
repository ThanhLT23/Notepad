package com.note.notepad.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.notepad.data.local.dao.NoteDao
import com.note.notepad.data.local.model.NoteItems
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val dao : NoteDao) : ViewModel() {
    private val _noteList = MutableStateFlow<List<NoteItems>>(emptyList())
    val noteList = _noteList.asStateFlow()

    init {
        loadNotes()
    }

    fun loadNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = dao.getAllNotes()
            _noteList.value = list
        }
    }

    fun addNotes(title: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newNote = NoteItems(title = title, content = content)
            dao.insertNote(newNote)
            loadNotes()
        }
    }

    fun updateNote(note: NoteItems, newTitle: String, newContent: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val updateNote = note.copy(title = newTitle, content = newContent)
            dao.updateNote(updateNote)
            loadNotes()
        }
    }

    fun deleteNote(note: NoteItems) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.deleteNote(note)
            loadNotes()
        }
    }
}