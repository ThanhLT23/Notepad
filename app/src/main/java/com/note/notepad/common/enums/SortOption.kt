package com.note.notepad.common.enums

enum class SortOption {
    TIME_EDITED_DESC,
    TIME_EDITED_ASC,
    TITLE_A_TO_Z,
    TITLE_Z_TO_A,
    TIME_CREATED_DESC,
    TIME_CREATED_ASC,
    COLOR;

    companion object {
        fun fromInt(value: Int): SortOption {
            return entries.getOrNull(value) ?: TIME_EDITED_DESC
        }
    }
}