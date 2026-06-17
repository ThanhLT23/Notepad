package com.note.notepad.app

import android.app.Application
import com.note.notepad.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androix.startup.KoinStartup
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

@OptIn(KoinExperimentalAPI::class)
class NotepadApplication : Application(), KoinStartup {
    override fun onKoinStartup(): KoinConfiguration = KoinConfiguration {
        androidLogger()
        androidContext(this@NotepadApplication)
        modules(appModule)
    }
}