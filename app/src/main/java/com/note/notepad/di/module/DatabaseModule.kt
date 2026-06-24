package com.note.notepad.di.module

import android.app.Application
import androidx.room.Room
import com.note.notepad.data.local.dao.CategoryDao
import com.note.notepad.data.local.dao.NoteDao
import com.note.notepad.data.local.database.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single { provideDatabase(androidApplication()) }
    single { provideNoteDao(get()) }
    single { provideCateDao(get()) }
}

fun provideDatabase(application: Application): AppDatabase {
    return Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "notepad_db"
    ).build()
}

fun provideNoteDao(database: AppDatabase): NoteDao {
    return database.noteDao()
}
fun provideCateDao(database: AppDatabase): CategoryDao {
    return database.categoryDao()
}