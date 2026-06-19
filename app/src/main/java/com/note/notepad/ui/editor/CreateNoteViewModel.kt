package com.note.notepad.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.notepad.data.local.model.NoteItems
import com.note.notepad.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateNoteViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _currentNote = MutableStateFlow<NoteItems?>(null)
    val currentNote = _currentNote.asStateFlow()



    fun loadData(noteId: Int) {
        if (noteId == -1) return

        viewModelScope.launch {
            val note = repository.getNoteById(noteId)
            _currentNote.value = note
        }
    }

    fun saveNote(title: String, content: String) {
        val newTitle = title.ifBlank { "Untitled" }
        val current = _currentNote.value

        viewModelScope.launch {
            if (current == null) {
                val newNote = NoteItems(
                    title = newTitle,
                    content = content,
                    lastTime = System.currentTimeMillis(),
                    creationTime = System.currentTimeMillis()
                )
                val newId = repository.insertNote(newNote)
                _currentNote.value = newNote.copy(id = newId.toInt())
            } else {
                val updateNote = current.copy(
                    title = newTitle,
                    content = content,
                    lastTime = System.currentTimeMillis()
                )
                repository.updateNote(updateNote)
                _currentNote.value = updateNote
            }
        }
    }

    fun deleteNote() {
        val currentId = currentNote.value?.id
        if (currentId != null && currentId > 0) {
            viewModelScope.launch {
                repository.softDelete(listOf(currentId))
            }
        }
    }


}