package com.note.notepad.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.notepad.data.local.model.NoteItems
import com.note.notepad.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _noteList = MutableStateFlow<List<NoteItems>>(emptyList())
    val noteList = _noteList.asStateFlow()
    private val _navigateToEdit = MutableSharedFlow<Int>()
    val navigateToEdit = _navigateToEdit.asSharedFlow()
    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllNote().collect { list ->
                _noteList.value = list
            }
        }
    }

    fun onFabClicked() {
        viewModelScope.launch {
            _navigateToEdit.emit(-1)
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
        val allIds = _noteList.value.map { it.id }.toSet()
        if (selectedIds.value.size == allIds.size && allIds.isNotEmpty()) {
            clearSelection()
        } else {
            _selectedIds.value = allIds
        }
    }

    fun selectAllNotes() {
        val ids = _noteList.value.map { it.id }.toSet()
        _isSelectionMode.value = true

        _selectedIds.value = ids
    }

    fun clearSelection() {
        _selectedIds.value = emptySet()
    }

    fun deleteSelectionNotes() {
        val deleteIds = _selectedIds.value.toList()
        if (deleteIds.isEmpty()) return

        viewModelScope.launch {
            repository.softDelete(deleteIds)
            clearSelection()
            _isSelectionMode.value = false
        }
    }

    fun sortByNewestTime() {
        viewModelScope.launch {
            repository.getSortedByTimeDesc().collect { list ->
                _noteList.value = list
            }
        }
    }

    fun sortByOldestTime() {
        viewModelScope.launch {
            repository.getSortedByTimeAsc().collect { list ->
                _noteList.value = list
            }
        }
    }

    fun sortByNewestCreation() {
        viewModelScope.launch {
            repository.getSortedByCreationDesc().collect { list ->
                _noteList.value = list
            }
        }
    }

    fun sortByOldestCreation() {
        viewModelScope.launch {
            repository.getSortedByCreationAsc().collect { list ->
                _noteList.value = list
            }
        }
    }

    fun sortByTitleAZ() {
        viewModelScope.launch {
            repository.getSortedByTitleAsc().collect { list ->
                _noteList.value = list
            }
        }
    }

    fun sortByTitleZA() {
        viewModelScope.launch {
            repository.getSortedByTitleDesc().collect { list ->
                _noteList.value = list
            }
        }
    }

}
