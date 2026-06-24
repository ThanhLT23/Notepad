package com.note.notepad.di.module

import com.note.notepad.ui.category.CategoryEditorViewModel
import com.note.notepad.ui.editor.CreateNoteViewModel
import com.note.notepad.ui.main.MainViewModel
import com.note.notepad.ui.trash.TrashViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module{
    viewModelOf(::MainViewModel)
    viewModelOf(::CreateNoteViewModel)
    viewModelOf(::TrashViewModel)
    viewModelOf(::CategoryEditorViewModel)
}
