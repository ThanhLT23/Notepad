package com.note.notepad.di.module

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import com.note.notepad.R
import com.note.notepad.data.local.dao.CategoryDao
import com.note.notepad.data.local.dao.NoteDao
import com.note.notepad.data.local.database.AppDatabase
import com.note.notepad.data.local.model.NoteItems
import com.note.notepad.data.local.prefs.AppPreferences
import com.note.notepad.utils.AppConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single { provideDatabase(androidApplication()) }
    single { provideNoteDao(get()) }
    single { provideCateDao(get()) }
    single { AppPreferences(androidContext()) }
}

fun provideDatabase(application: Application): AppDatabase {
    var appDatabase: AppDatabase? = null

    appDatabase = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        AppConstant.DATABASE_NAME
    )
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(connection: SQLiteConnection) {
                super.onCreate(connection)

                CoroutineScope(Dispatchers.IO).launch {
                    val now = System.currentTimeMillis()
                    val tutorialNote = NoteItems(
                        title = application.getString(R.string.welcome_title),
                        content = application.getString(R.string.welcome_content),
                        lastTime = now,
                        creationTime = now,
                        color = 0
                    )
                    appDatabase?.noteDao()?.insertNote(tutorialNote)
                }
            }
        })
        .build()
    return appDatabase
}

fun provideNoteDao(database: AppDatabase): NoteDao {
    return database.noteDao()
}

fun provideCateDao(database: AppDatabase): CategoryDao {
    return database.categoryDao()
}