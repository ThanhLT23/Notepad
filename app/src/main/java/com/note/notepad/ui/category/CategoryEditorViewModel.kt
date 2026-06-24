package com.note.notepad.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.note.notepad.data.local.model.CategoryItems
import com.note.notepad.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryEditorViewModel(private val repository: CategoryRepository): ViewModel() {
    private val _currentCate = MutableStateFlow<CategoryItems?>(null)
    val currentCate = _currentCate.asStateFlow()
    private val _cateList = MutableStateFlow<List<CategoryItems>>(emptyList())

    val cateList = repository.getAllCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList<CategoryItems>()
    )

    fun addCategory(name: String) {
        val newName = name.trim()
        if (newName.isBlank()) return

        viewModelScope.launch {
                val currentPositionIndex = repository.getMaxPosition() ?: 0
                val newCate = CategoryItems(
                    name = newName,
                    position = currentPositionIndex + 1
                )
                repository.insertCategory(newCate)
        }
    }

    fun editCategory(category: CategoryItems, newName: String) {
        val trimmedName = newName.trim()

        if (trimmedName.isBlank() || trimmedName == category.name) {
            return
        }
        viewModelScope.launch {
            val updatedCate = category.copy(
                name = trimmedName
            )
            repository.updateCategory(updatedCate)
        }
    }

    fun deleteCategory(category: CategoryItems) {
            viewModelScope.launch {
                repository.deleteCategory(category)
            }
    }

    fun updateCategoryPositions(newList: List<CategoryItems>) {
        viewModelScope.launch {
            val updatedList = newList.mapIndexed { index, category ->
                category.copy(position = index)
            }
            repository.updateCategories(updatedList)
        }
    }
}