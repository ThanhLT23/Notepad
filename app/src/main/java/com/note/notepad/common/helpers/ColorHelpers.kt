package com.note.notepad.common.helpers

import android.content.Context
import androidx.core.content.ContextCompat
import com.note.notepad.R
import com.note.notepad.data.local.model.ColorSet


object ColorHelpers {

    private data class ColorMapper(
        val mainColor: Int,
        val start: Int,
        val end: Int,
        val startSelected: Int,
        val selected: Int,
        val border: Int
    )

    private val colorConfigs = listOf(
        ColorMapper(
            R.color.color_light_peach_pink,
            R.color.note_pink_start,
            R.color.color_light_peach_pink,
            R.color.note_pink_start_selected,
            R.color.note_pink_selected,
            R.color.note_pink_border
        ),
        ColorMapper(
            R.color.color_peach_orange,
            R.color.note_orange_start,
            R.color.color_peach_orange,
            R.color.note_orange_start_selected,
            R.color.note_orange_selected,
            R.color.note_orange_border
        ),
        ColorMapper(
            R.color.color_pastel_yellow,
            R.color.note_yellow_start,
            R.color.color_pastel_yellow,
            R.color.note_yellow_start_selected,
            R.color.note_yellow_selected,
            R.color.note_yellow_border
        ),
        ColorMapper(
            R.color.color_pastel_green,
            R.color.note_green_start,
            R.color.color_pastel_green,
            R.color.note_green_start_selected,
            R.color.note_green_selected,
            R.color.note_green_border
        ),
        ColorMapper(
            R.color.color_pastel_aqua,
            R.color.note_aqua_start,
            R.color.color_pastel_aqua,
            R.color.note_aqua_start_selected,
            R.color.note_aqua_selected,
            R.color.note_aqua_border
        ),
        ColorMapper(
            R.color.color_soft_blue,
            R.color.note_blue_start,
            R.color.color_soft_blue,
            R.color.note_blue_start_selected,
            R.color.note_blue_selected,
            R.color.note_blue_border
        ),
        ColorMapper(
            R.color.color_soft_purple,
            R.color.note_purple_start,
            R.color.color_soft_purple,
            R.color.note_purple_start_selected,
            R.color.note_purple_selected,
            R.color.note_purple_border
        ),
        ColorMapper(
            R.color.color_pastel_pink,
            R.color.note_pastel_pink_start,
            R.color.color_pastel_pink,
            R.color.note_pastel_pink_start_selected,
            R.color.note_pastel_pink_selected,
            R.color.note_pastel_pink_border
        ),
        ColorMapper(
            R.color.color_off_white,
            R.color.note_white_start,
            R.color.color_off_white,
            R.color.note_white_start_selected,
            R.color.note_white_selected,
            R.color.note_white_border
        ),
        ColorMapper(
            R.color.color_pastel_blue,
            R.color.note_pastel_blue_start,
            R.color.color_pastel_blue,
            R.color.note_pastel_blue_start_selected,
            R.color.note_pastel_blue_selected,
            R.color.note_pastel_blue_border
        ),
        ColorMapper(
            R.color.color_light_aqua,
            R.color.note_light_aqua_start,
            R.color.color_light_aqua,
            R.color.note_light_aqua_start_selected,
            R.color.note_light_aqua_selected,
            R.color.note_light_aqua_border
        ),
        ColorMapper(
            R.color.color_cream_white,
            R.color.note_cream_white_start,
            R.color.color_cream_white,
            R.color.note_cream_white_start_selected,
            R.color.note_cream_white_selected,
            R.color.note_cream_white_border
        ),
        ColorMapper(
            R.color.color_vanilla,
            R.color.note_vanilla_start,
            R.color.color_vanilla,
            R.color.note_vanilla_start_selected,
            R.color.note_vanilla_selected,
            R.color.note_vanilla_border
        ),
        ColorMapper(
            R.color.color_soft_rose,
            R.color.note_soft_rose_start,
            R.color.color_soft_rose,
            R.color.note_soft_rose_start_selected,
            R.color.note_soft_rose_selected,
            R.color.note_soft_rose_border
        ),
        ColorMapper(
            R.color.color_light_lavender,
            R.color.note_lavender_start,
            R.color.color_light_lavender,
            R.color.note_lavender_start_selected,
            R.color.note_lavender_selected,
            R.color.note_lavender_border
        ),
        ColorMapper(
            R.color.color_muted_blue,
            R.color.note_muted_blue_start,
            R.color.color_muted_blue,
            R.color.note_muted_blue_start_selected,
            R.color.note_muted_blue_selected,
            R.color.note_muted_blue_border
        ),
        ColorMapper(
            R.color.color_mist_blue,
            R.color.note_mist_blue_start,
            R.color.color_mist_blue,
            R.color.note_mist_blue_start_selected,
            R.color.note_mist_blue_selected,
            R.color.note_mist_blue_border
        ),
        ColorMapper(
            R.color.color_mint_aqua,
            R.color.note_mint_aqua_start,
            R.color.color_mint_aqua,
            R.color.note_mint_aqua_start_selected,
            R.color.note_mint_aqua_selected,
            R.color.note_mint_aqua_border
        ),
        ColorMapper(
            R.color.color_mist_green,
            R.color.note_mist_green_start,
            R.color.color_mist_green,
            R.color.note_mist_green_start_selected,
            R.color.note_mist_green_selected,
            R.color.note_mist_green_border
        ),
        ColorMapper(
            R.color.color_dusty_rose,
            R.color.note_dusty_rose_start,
            R.color.color_dusty_rose,
            R.color.note_dusty_rose_start_selected,
            R.color.note_dusty_rose_selected,
            R.color.note_dusty_rose_border
        ),
        ColorMapper(
            R.color.color_plum_purple,
            R.color.note_plum_purple_start,
            R.color.color_plum_purple,
            R.color.note_plum_purple_start_selected,
            R.color.note_plum_purple_selected,
            R.color.note_plum_purple_border
        ),
        ColorMapper(
            R.color.color_berry_pink,
            R.color.note_berry_pink_start,
            R.color.color_berry_pink,
            R.color.note_berry_pink_start_selected,
            R.color.note_berry_pink_selected,
            R.color.note_berry_pink_border
        ),
        ColorMapper(
            R.color.color_coral_red,
            R.color.note_coral_red_start,
            R.color.color_coral_red,
            R.color.note_coral_red_start_selected,
            R.color.note_coral_red_selected,
            R.color.note_coral_red_border
        ),
        ColorMapper(
            R.color.color_coral_orange,
            R.color.note_coral_orange_start,
            R.color.color_coral_orange,
            R.color.note_coral_orange_start_selected,
            R.color.note_coral_orange_selected,
            R.color.note_coral_orange_border
        ),ColorMapper(
            R.color.color_golden_yellow,
            R.color.note_golden_yellow_start,
            R.color.color_golden_yellow,
            R.color.note_golden_yellow_start_selected,
            R.color.note_golden_yellow_selected,
            R.color.note_golden_yellow_border
        ),ColorMapper(
            R.color.color_honey_cream,
            R.color.note_honey_cream_start,
            R.color.color_honey_cream,
            R.color.note_honey_cream_start_selected,
            R.color.note_honey_cream_selected,
            R.color.note_honey_cream_border
        )
    )

    fun getNoteColorSet(context: Context, noteColor: Int): ColorSet {
        val config =
            colorConfigs.find { ContextCompat.getColor(context, it.mainColor) == noteColor }
        return if (config != null) {
            ColorSet(
                ContextCompat.getColor(context, config.start),
                ContextCompat.getColor(context, config.end),
                ContextCompat.getColor(context, config.startSelected),
                ContextCompat.getColor(context, config.selected),
                ContextCompat.getColor(context, config.border)
            )
        } else {
            ColorSet(
                ContextCompat.getColor(context, R.color.note_bg_start),
                ContextCompat.getColor(context, R.color.note_bg_end),
                ContextCompat.getColor(context, R.color.note_bg_start),
                ContextCompat.getColor(context, R.color.bg_note_selected),
                ContextCompat.getColor(context, R.color.note_border)
            )
        }
    }
}