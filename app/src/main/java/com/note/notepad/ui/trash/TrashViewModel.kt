package com.note.notepad.ui.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.notepad.data.local.model.NoteItems
import com.note.notepad.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TrashViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _deletedList = MutableStateFlow<List<NoteItems>>(emptyList())
    val deleteList = _deletedList.asStateFlow()
    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getDeletedNote().collect { list ->
                _deletedList.value = list
            }
        }
    }

    fun onSelection(noteId: Int) {
        if (!_isSelectionMode.value) {
            _isSelectionMode.value = true
        }
        val currentSelected = _selectedIds.value.toMutableSet()
        if (currentSelected.contains(noteId)) {
            currentSelected.remove(noteId)
        } else {
            currentSelected.add(noteId)
        }
        _selectedIds.value = currentSelected
    }

    fun exitSelectionMode() {
        _isSelectionMode.value = false
        clearSelection()
    }

    fun selectAll() {
        val allIds = _deletedList.value.map { it.id }.toSet()
        if (selectedIds.value.size == allIds.size && allIds.isNotEmpty()) {
            clearSelection()
        } else {
            _selectedIds.value = allIds
        }
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun restoreItem(noteId: Int? = null) {
        viewModelScope.launch {
            if (noteId != null) {
                repository.restoreItem(listOf(noteId))
            } else {
                val deleteIds = _selectedIds.value.toList()
                if (deleteIds.isEmpty()) return@launch
                repository.restoreItem(deleteIds)
                exitSelectionMode()
            }
        }
    }

    fun hardDelete(noteId: Int? = null) {
        viewModelScope.launch {
            if (noteId != null) {
                repository.hardDelete(listOf(noteId))
            } else {
                val deleteIds = _selectedIds.value.toList()
                if (deleteIds.isEmpty()) return@launch
                repository.hardDelete(deleteIds)
                exitSelectionMode()
            }
        }
    }

    fun clearTrash() {
        viewModelScope.launch {
            repository.clearTrash()
            exitSelectionMode()
        }
    }

    fun restoreAllTrash() {
        viewModelScope.launch {
            repository.restoreAllTrash()
            exitSelectionMode()
        }
    }

}
