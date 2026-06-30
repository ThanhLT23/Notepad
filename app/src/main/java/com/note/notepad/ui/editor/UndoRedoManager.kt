package com.note.notepad.ui.editor

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.util.LinkedList

class UndoRedoManager(private val editText: EditText) {
    private val undoStack = LinkedList<String>()
    private val redoStack = LinkedList<String>()

    private var lastSavedText = ""
    private var isTextChangedByCode = false

    private var textBeforeChange = ""
    var onStateChanged: (() -> Unit)? = null

    init {
        lastSavedText = editText.text.toString()
        undoStack.push(lastSavedText)

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (!isTextChangedByCode) {
                    textBeforeChange = s?.toString() ?: ""
                }
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (isTextChangedByCode) return
                val currentText = s.toString()
                val isSpaceOrEnter = count == 1 && start < currentText.length &&
                        (currentText[start] == ' ' || currentText[start] == '\n')
                val isBulkChange = count > 1 || before > 1
                if (isSpaceOrEnter ||isBulkChange) {
                    saveHistory(textBeforeChange, currentText)
                }
                onStateChanged?.invoke()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun saveHistory(textBeforeChange: String, currentText: String) {
        var isChanged = false
        if (textBeforeChange!= lastSavedText) {
            undoStack.push(textBeforeChange)
            lastSavedText = textBeforeChange
            isChanged = true
        }
        if (currentText!=lastSavedText) {
            undoStack.push(currentText)
            lastSavedText = currentText
            isChanged = true
        }
        if (isChanged) {
            redoStack.clear()
        }
        onStateChanged?.invoke()
    }

    fun undo() {
        val currentText = editText.text.toString()
        if (currentText == lastSavedText && undoStack.size > 1) {
            val poppedText = undoStack.pop()
            redoStack.push(poppedText)
            val previousText = undoStack.peek() ?: ""
            updateText(previousText)
        } else if (currentText != lastSavedText) {
            redoStack.push(currentText)
            val previousText = undoStack.peek() ?: ""
            updateText(previousText)
        }
        onStateChanged?.invoke()
    }

    fun redo() {
        if (redoStack.isEmpty()) return
        val nextText = redoStack.pop()
        undoStack.push(nextText)
        updateText(nextText)
        onStateChanged?.invoke()
    }

    private fun updateText(newText: String) {
        isTextChangedByCode = true
        editText.setText(newText)
        editText.setSelection(newText.length)
        lastSavedText = newText
        isTextChangedByCode = false
    }

    fun saveCheckpoints() {
        isTextChangedByCode = true
        undoStack.clear()
        redoStack.clear()
        lastSavedText = editText.text.toString()
        undoStack.push(lastSavedText)
        textBeforeChange = lastSavedText
        isTextChangedByCode = false
        onStateChanged?.invoke()
    }

    fun undoAll() {
        if (undoStack.isEmpty()) return
        val initText = undoStack.last()
        val currentText = editText.text.toString()
        if (currentText == initText) return
        undoStack.clear()
        redoStack.clear()
        undoStack.push(initText)
        updateText(initText)
        onStateChanged?.invoke()
    }

    fun canUndo(): Boolean {
        val currentText = editText.text.toString()
        return currentText != lastSavedText || undoStack.size > 1
    }

    fun canRedo(): Boolean {
        return redoStack.isNotEmpty()
    }
}