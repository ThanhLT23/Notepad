package com.note.notepad.data.local.model.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.note.notepad.data.local.model.CategoryItems
import com.note.notepad.data.local.model.CategoryNoteRef
import com.note.notepad.data.local.model.NoteItems

data class NoteWithCategories(
    @Embedded val note: NoteItems,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(CategoryNoteRef::class, parentColumn = "noteId", entityColumn = "categoryId")
    )
    val category: List<CategoryItems>
)
