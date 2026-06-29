package com.note.notepad.common.helpers

import android.content.Context
import androidx.core.content.ContextCompat
import com.note.notepad.R
import com.note.notepad.data.local.model.ColorSet


object ColorHelpers {
    fun getNoteColorSet(context: Context, noteColor: Int): ColorSet {
        return when (noteColor) {
            ContextCompat.getColor(context, R.color.color_light_peach_pink) -> ColorSet(
                ContextCompat.getColor(context, R.color.note_pink_start),
                ContextCompat.getColor(context, R.color.color_light_peach_pink),
                ContextCompat.getColor(context, R.color.note_pink_start_selected),
                ContextCompat.getColor(context, R.color.note_pink_selected),
                ContextCompat.getColor(context, R.color.note_pink_border)
            )
            ContextCompat.getColor(context, R.color.color_peach_orange) -> ColorSet(
                ContextCompat.getColor(context, R.color.note_orange_start),
                ContextCompat.getColor(context, R.color.color_peach_orange),
                ContextCompat.getColor(context, R.color.note_orange_start_selected),
                ContextCompat.getColor(context, R.color.note_orange_selected),
                ContextCompat.getColor(context, R.color.note_orange_border)
            )
            ContextCompat.getColor(context, R.color.color_pastel_yellow) -> ColorSet(
                ContextCompat.getColor(context, R.color.note_yellow_start),
                ContextCompat.getColor(context, R.color.color_pastel_yellow),
                ContextCompat.getColor(context, R.color.note_yellow_start_selected),
                ContextCompat.getColor(context, R.color.note_yellow_selected),
                ContextCompat.getColor(context, R.color.note_yellow_border)
            )
            ContextCompat.getColor(context, R.color.color_pastel_green) -> ColorSet(
                ContextCompat.getColor(context, R.color.note_green_start),
                ContextCompat.getColor(context, R.color.color_pastel_green),
                ContextCompat.getColor(context, R.color.note_green_start_selected),
                ContextCompat.getColor(context, R.color.note_green_selected),
                ContextCompat.getColor(context, R.color.note_green_border)
            )
            ContextCompat.getColor(context, R.color.color_pastel_aqua) -> ColorSet(
                ContextCompat.getColor(context, R.color.note_aqua_start),
                ContextCompat.getColor(context, R.color.color_pastel_aqua),
                ContextCompat.getColor(context, R.color.note_aqua_start_selected),
                ContextCompat.getColor(context, R.color.note_aqua_selected),
                ContextCompat.getColor(context, R.color.note_aqua_border)
            )
            ContextCompat.getColor(context, R.color.color_soft_blue) -> ColorSet(
                ContextCompat.getColor(context, R.color.note_blue_start),
                ContextCompat.getColor(context, R.color.color_soft_blue),
                ContextCompat.getColor(context, R.color.note_blue_start_selected),
                ContextCompat.getColor(context, R.color.note_blue_selected),
                ContextCompat.getColor(context, R.color.note_blue_border)
            )
            else -> ColorSet(
                ContextCompat.getColor(context, R.color.note_bg_start),
                ContextCompat.getColor(context, R.color.note_bg_end),
                ContextCompat.getColor(context, R.color.note_bg_start),
                ContextCompat.getColor(context, R.color.bg_note_selected),
                ContextCompat.getColor(context, R.color.note_border)
            )
        }
    }
}