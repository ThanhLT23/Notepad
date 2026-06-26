package com.note.notepad.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.note.notepad.data.local.model.CategoryNoteRef
import com.note.notepad.data.local.model.NoteItems
import com.note.notepad.data.local.model.relation.NoteWithCategories
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0")
    fun getAllNotes(): Flow<List<NoteItems>>

    @Query("SELECT * FROM notes WHERE id = :noteId LIMIT 1")
    suspend fun getNoteById(noteId: Int): NoteItems

    @Insert
    suspend fun insertNote(note: NoteItems) : Long

    @Update
    suspend fun updateNote(note: NoteItems)

    @Query("UPDATE notes SET isDeleted = 1, isSelected = 0 WHERE id IN (:ids)")
    suspend fun softDelete(ids: List<Int>)

    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    fun getDeletedNotes(): Flow<List<NoteItems>>

    @Query("UPDATE notes SET isDeleted = 0, isSelected = 0 WHERE id IN (:ids)")
    suspend fun restoreItem(ids: List<Int>)

    @Query("DELETE FROM notes WHERE id IN (:ids)")
    suspend fun hardDelete(ids: List<Int>)

    @Query("DELETE FROM notes WHERE isDeleted = 1")
    suspend fun clearTrash()

    @Query("UPDATE notes SET isDeleted = 0 WHERE isDeleted = 1")
    suspend fun restoreAllTrash()

    @Transaction
    @Query("SELECT * FROM notes WHERE isDeleted = 0")
    fun getNotesWithCategories(): Flow<List<NoteWithCategories>>

    @Transaction
    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    fun getDeletedNotesWithCategories(): Flow<List<NoteWithCategories>>

    @Query("DELETE FROM category_note_ref WHERE noteId = :noteId")
    suspend fun deleteCategoriesForNote(noteId: Int)

    @Transaction
    suspend fun updateNoteCategories(noteId: Int, categoryIds: List<Int>) {
        deleteCategoriesForNote(noteId)
        categoryIds.forEach { catId ->
            insertCategoryNoteRef(CategoryNoteRef(noteId, catId))
        }
    }
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoryNoteRef(ref: CategoryNoteRef)

    @Transaction
    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNoteWithCategoriesById(noteId: Int): Flow<NoteWithCategories?>

}
