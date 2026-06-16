package com.note.notepad.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.note.notepad.data.local.dao.NoteDao
import com.note.notepad.data.local.model.NoteItems

@Database(entities = [NoteItems::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notepad_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}