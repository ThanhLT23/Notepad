package com.note.notepad.di.module

import com.note.notepad.data.repository.CategoryRepository
import com.note.notepad.data.repository.NoteRepository
import org.koin.dsl.module

val repositoryModule = module{
    single { NoteRepository(get())}
    single { CategoryRepository(get()) }
}