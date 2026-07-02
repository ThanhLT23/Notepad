package com.note.notepad.data.local.prefs

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("note_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TUTORIAL_KEY = "is_tutorial"
    }

    var isTutorial: Boolean
        get() = prefs.getBoolean(TUTORIAL_KEY, false)
        set(value) = prefs.edit().putBoolean(TUTORIAL_KEY, value).apply()
}