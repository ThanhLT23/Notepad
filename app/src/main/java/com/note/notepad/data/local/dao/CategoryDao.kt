package com.note.notepad.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.note.notepad.data.local.model.CategoryItems
import com.note.notepad.data.local.model.CategoryNoteRef
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY position ASC")
    fun getAllCategories(): Flow<List<CategoryItems>>

    @Query("SELECT * FROM categories WHERE id = :categoryId LIMIT 1")
    fun getCategoryById(categoryId: Int): CategoryItems

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryItems)

    @Query("SELECT MAX(position) FROM categories")
    suspend fun getMaxPosition(): Int?

    @Update
    suspend fun updateCategory(category: CategoryItems)

    @Update
    suspend fun updateCategories(categories: List<CategoryItems>)

    @Delete
    suspend fun deleteCategory(category: CategoryItems)
}