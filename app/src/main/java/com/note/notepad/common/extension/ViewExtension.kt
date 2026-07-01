package com.note.notepad.common.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.view.isGone

fun View.hideKeyboard() {
    clearFocus()

    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun TextView.setTextOrGone(
    text: String,
    visible: Boolean = text.isNotBlank()
) {
    isGone = !visible
    if (visible) this.text = text
}