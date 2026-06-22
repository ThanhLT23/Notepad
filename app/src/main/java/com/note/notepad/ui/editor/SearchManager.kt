package com.note.notepad.ui.editor

import android.text.Spannable
import android.text.style.BackgroundColorSpan
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.note.notepad.R


class SearchManager(private val editText: EditText) {
    private var searchResult = mutableListOf<Pair<Int, Int>>()
    private var currentIndex = -1

    fun onSearch(query: String, onCountUpdate: (String) -> Unit) {
        val editable = editText.text ?: return
        searchResult.clear()
        currentIndex = -1

        val oldSpan = editable.getSpans(0, editable.length, BackgroundColorSpan::class.java)
        for (span in oldSpan)   editable.removeSpan(span)
        if (query.isEmpty()) {
            onCountUpdate("0/0")
            return
        }

        val lowerText = editable.toString().lowercase()
        val lowerQuery = query.lowercase()
        var startPos = lowerText.indexOf(lowerQuery)

        while (startPos >= 0) {
            val endPos = startPos + query.length
            searchResult.add(Pair(startPos, endPos))
            startPos = lowerText.indexOf(lowerQuery, endPos)
        }

        if (searchResult.isNotEmpty()) {
            currentIndex = 0
            applyHighlight()
        }
        onCountUpdate(getCountText())
    }

    private fun getCountText(): String {
        return if (searchResult.isEmpty()) "0/0" else "${currentIndex + 1}/${searchResult.size}"
    }

    private fun applyHighlight() {
        val editable = editText.text ?: return
        val context = editText.context
        val highlightColor = ContextCompat.getColor(context, R.color.highlightColor)
        val highlightSelect = ContextCompat.getColor(context, R.color.highlightSelect)

        val oldSpans = editable.getSpans(0, editable.length, BackgroundColorSpan::class.java)
        for (span in oldSpans) editable.removeSpan(span)

        for (i in searchResult.indices) {
            val pair = searchResult[i]
            val color = if (i == currentIndex) highlightSelect else highlightColor

            editable.setSpan(
                BackgroundColorSpan(color),
                pair.first, pair.second,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        if (currentIndex >= 0 && currentIndex < searchResult.size) {
            editText.requestFocus()
            editText.setSelection(searchResult[currentIndex].first)
        }
    }

    fun navigateSearch(isForward: Boolean, onCountUpdate: (String) -> Unit) {
        if (searchResult.isEmpty()) return
        if (isForward) {
            currentIndex = (currentIndex + 1) % searchResult.size
        } else {
            currentIndex = if (currentIndex - 1 < 0) searchResult.size - 1 else currentIndex -1
        }
        applyHighlight()
        onCountUpdate(getCountText())
    }

    fun clearSearch() {
        onSearch("", {})
    }

}