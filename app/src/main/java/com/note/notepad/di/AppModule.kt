package com.note.notepad.di

import com.note.notepad.di.module.databaseModule
import com.note.notepad.di.module.repositoryModule
import com.note.notepad.di.module.viewModelModule

val appModule = listOf(
    databaseModule,
    repositoryModule,
    viewModelModule
)