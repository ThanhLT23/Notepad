package com.note.notepad.data.repository

import com.note.notepad.data.local.dao.CategoryDao
import com.note.notepad.data.local.model.CategoryItems
import com.note.notepad.data.local.model.CategoryNoteRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CategoryRepository(private val dao: CategoryDao) {
    fun getAllCategories(): Flow<List<CategoryItems>> = dao.getAllCategories()

    suspend fun getCategoryById(id: Int): CategoryItems = withContext(Dispatchers.IO) {
        dao.getCategoryById(id)
    }

    suspend fun getMaxPosition(): Int? = withContext(Dispatchers.IO) {
        dao.getMaxPosition()
    }

    suspend fun insertCategory(category: CategoryItems) = withContext(Dispatchers.IO) {
       dao.insertCategory(category)
    }

    suspend fun updateCategory(category: CategoryItems) = withContext(Dispatchers.IO) {
        dao.updateCategory(category)
    }

    suspend fun updateCategories(categories: List<CategoryItems>) = withContext(Dispatchers.IO) {
        dao.updateCategories(categories)
    }

    suspend fun deleteCategory(category: CategoryItems) = withContext(Dispatchers.IO) {
        dao.deleteCategory(category)
    }

}