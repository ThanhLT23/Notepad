package com.note.notepad.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.notepad.common.enums.SortOption
import com.note.notepad.data.local.model.NoteItems
import com.note.notepad.data.local.prefs.AppPreferences
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
    categoryRepository: CategoryRepository,
    private val appPreferences: AppPreferences
) : ViewModel() {
    private val _navigateToEdit = MutableSharedFlow<Pair<Int, Int>>()
    val navigateToEdit = _navigateToEdit.asSharedFlow()
    private val _selectedIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedIds = _selectedIds.asStateFlow()
    private val _isSelectionMode = MutableStateFlow(false)
    val isSelectionMode = _isSelectionMode.asStateFlow()
    private val _sortOption = MutableStateFlow(SortOption.TIME_EDITED_DESC)
    val sortOption = _sortOption.asStateFlow()
    private val _searchOption = MutableStateFlow("")
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _categoryId = MutableStateFlow(-1)
    val categoryId = _categoryId.asStateFlow()
    private var colorOrderList: List<Int> = emptyList()
    private val _showTutorial = MutableStateFlow(!appPreferences.isTutorial)
    val showTutorial = _showTutorial.asStateFlow()

    fun completeTutorial() {
        if (_showTutorial.value) {
            appPreferences.isTutorial = true
            _showTutorial.value = false
        }
    }
    fun setCategory(id: Int) {
        _categoryId.value = id
    }

    val noteList = combine(
        repository.getAllNotesWithCategories(), _sortOption, _searchOption, _categoryId
    ) { noteWithCat, option, query, catId ->
        val categoryFiltered = when (catId) {
            -1 -> noteWithCat
            -2 -> noteWithCat.filter { it.category.isEmpty() }
            else -> noteWithCat.filter { noteObj ->
                noteObj.category.any { it.id == catId }
            }
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
            SortOption.TIME_EDITED_DESC -> searchFiltered.sortedByDescending { it.note.lastTime }
            SortOption.TIME_EDITED_ASC -> searchFiltered.sortedBy { it.note.lastTime }
            SortOption.TITLE_A_TO_Z -> searchFiltered.sortedByDescending { it.note.title }
            SortOption.TITLE_Z_TO_A -> searchFiltered.sortedBy { it.note.title }
            SortOption.TIME_CREATED_DESC -> searchFiltered.sortedByDescending { it.note.creationTime }
            SortOption.TIME_CREATED_ASC -> searchFiltered.sortedBy { it.note.creationTime }
            SortOption.COLOR -> {
                searchFiltered.sortedBy { item ->
                    val index = colorOrderList.indexOf(item.note.color)
                    if (index == -1) Int.MAX_VALUE else index
                }
            }
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

    fun onFabClicked(defaultTitle: String) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val newNote = NoteItems(
                title = defaultTitle,
                content = "",
                lastTime = now,
                creationTime = now,
                color = 0
            )
            val newNoteId = repository.insertNote(newNote).toInt()

            val currentCatId = _categoryId.value
            if (currentCatId != -1 && currentCatId != -2) {
                repository.updateNoteCategories(newNoteId, listOf(currentCatId))
            }
            _navigateToEdit.emit(Pair(newNoteId, currentCatId))
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
        _sortOption.value = SortOption.fromInt(option)
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

    fun updateColorOrder(colors: List<Int>) {
        this.colorOrderList = colors
    }


}
