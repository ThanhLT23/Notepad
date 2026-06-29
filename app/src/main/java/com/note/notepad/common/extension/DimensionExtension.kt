package com.note.notepad.common.extension

import android.content.Context

fun Context.dp(value: Int): Int =
    (value * resources.displayMetrics.density).toInt()

fun Context.dp(value: Float): Float =
    value * resources.displayMetrics.density