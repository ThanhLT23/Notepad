package com.note.notepad.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.notepad.data.local.model.NoteItems
import com.note.notepad.data.repository.CategoryRepository
import com.note.notepad.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

class MainViewModel(
    private val repository: NoteRepository,
    categoryRepository: CategoryRepository) : ViewModel() {
    private val _noteList = MutableStateFlow<List<NoteItems>>(emptyList())
    private val _navigateToEdit = MutableSharedFlow<Int>()
    val navigateToEdit = _navigateToEdit.asSharedFlow()
    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()
    private val _sortOption = MutableStateFlow(0)
    val sortOption = _sortOption.asStateFlow()
    private val _searchOption = MutableStateFlow("")
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val noteList = combine(
        repository.getAllNote(), _sortOption, _searchOption
    ) { note, option, query ->
        val filteredNotes = if (query.isBlank()) {
            note
        } else {
            note.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.content.contains(query, ignoreCase = true)
            }
        }
        when (option) {
            0 -> filteredNotes.sortedByDescending { it.lastTime }
            1 -> filteredNotes.sortedBy { it.lastTime }
            2 -> filteredNotes.sortedByDescending { it.title }
            3 -> filteredNotes.sortedBy { it.title }
            4 -> filteredNotes.sortedByDescending { it.creationTime }
            5 -> filteredNotes.sortedBy { it.creationTime }
            else -> filteredNotes
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList<NoteItems>()
    )

    val categories = categoryRepository.getAllCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )
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

    fun sortNotes(option: Int) {
        _sortOption.value = option
    }

    fun searchNotes(query: String) {
        _searchOption.value = query
        _searchQuery.value = query
    }

}
