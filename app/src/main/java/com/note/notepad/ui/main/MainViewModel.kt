package com.note.notepad.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val _categoryId = MutableStateFlow(-1)
    val categoryId = _categoryId.asStateFlow()

    fun setCategory(id: Int) {
        _categoryId.value = id
    }

    val noteList = combine(
        repository.getAllNotesWithCategories(), _sortOption, _searchOption, _categoryId
    ) { noteWithCat, option, query, catId ->
        val categoryFiltered = when (catId) {
            -1 -> noteWithCat
            -2 -> noteWithCat.filter { it.category.isEmpty()}
            else -> noteWithCat.filter { noteObj ->
                noteObj.category.any { it.id == catId } }
        }

        val searchFiltered = if (query.isBlank()) {
            categoryFiltered
        } else {
            categoryFiltered.filter {
                it.note.title.contains(query, ignoreCase = true) ||
                        it.note.content.contains(query, ignoreCase = true)
            }
        }

        when (option) {
            0 -> searchFiltered.sortedByDescending { it.note.lastTime }
            1 -> searchFiltered.sortedBy { it.note.lastTime }
            2 -> searchFiltered.sortedByDescending { it.note.title }
            3 -> searchFiltered.sortedBy { it.note.title }
            4 -> searchFiltered.sortedByDescending { it.note.creationTime }
            5 -> searchFiltered.sortedBy { it.note.creationTime }
            else -> searchFiltered
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val categories = categoryRepository.getAllCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )
    fun onFabClicked() {
        viewModelScope.launch {
            _navigateToEdit.emit(_categoryId.value)
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
        val allIds = noteList.value.map { it.note.id }.toSet()
        if (selectedIds.value.size == allIds.size && allIds.isNotEmpty()) {
            clearSelection()
        } else {
            _selectedIds.value = allIds
        }
    }

    fun selectAllNotes() {
        val ids = noteList.value.map { it.note.id }.toSet()
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

    fun categorizeSelectedNotes(categoryIds: List<Int>) {
        viewModelScope.launch {
            _selectedIds.value.forEach { noteId ->
                repository.updateNoteCategories(noteId, categoryIds)
            }
            exitSelectionMode()
        }
    }

    fun colorizeSelectedNotes(color: Int) {
        val ids = _selectedIds.value.toList()
        if (ids.isEmpty()) return
        viewModelScope.launch {
            repository.updateNotesColor(ids, color)
            exitSelectionMode()
        }
    }

}
