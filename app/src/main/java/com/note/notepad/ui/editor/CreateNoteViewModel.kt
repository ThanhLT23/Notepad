package com.note.notepad.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.notepad.data.local.model.NoteItems
import com.note.notepad.data.repository.CategoryRepository
import com.note.notepad.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CreateNoteViewModel(
    private val repository: NoteRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {
    private val _currentNote = MutableStateFlow<NoteItems?>(null)
    val currentNote = _currentNote.asStateFlow()
    private val _selectedCategoryIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedCategoryIds = _selectedCategoryIds.asStateFlow()

    val allCategories = categoryRepository.getAllCategories()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun loadData(noteId: Int, recentCategoryId: Int = -1) {
        if (noteId == -1) {
            if (recentCategoryId != -1 && recentCategoryId != -2) {
                _selectedCategoryIds.value = listOf(recentCategoryId)
            }
            return
        }

        viewModelScope.launch {
            repository.getNoteWithCategoriesById(noteId).collect { noteWithCats ->
                _selectedCategoryIds.value = noteWithCats?.category?.map { it.id } ?: emptyList()
                _currentNote.value = noteWithCats?.note
            }
        }
    }

    fun saveNote(title: String, content: String) {
        val newTitle = title.ifBlank { "Untitled" }
        val current = _currentNote.value
        val categoryIds = _selectedCategoryIds.value
        val now = System.currentTimeMillis()

        viewModelScope.launch {
            if (current == null) {
                val newNote = NoteItems(
                    title = newTitle,
                    content = content,
                    lastTime = now,
                    creationTime = now
                )
                val noteId = repository.insertNote(newNote).toInt()
                repository.updateNoteCategories(noteId, categoryIds)
            } else {
                val updateNote = current.copy(
                    title = newTitle,
                    content = content,
                    lastTime = now
                )
                repository.updateNote(updateNote)
                repository.updateNoteCategories(updateNote.id, categoryIds)
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

    fun updateSelectedCategories(ids: List<Int>) {
        _selectedCategoryIds.value = ids
    }


}