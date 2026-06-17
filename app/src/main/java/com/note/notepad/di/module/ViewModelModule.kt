package com.note.notepad.di.module

import com.note.notepad.ui.editor.CreateNoteViewModel
import com.note.notepad.ui.main.MainViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module{
    viewModelOf(::MainViewModel)
    viewModelOf(::CreateNoteViewModel)
}