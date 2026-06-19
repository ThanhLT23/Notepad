package com.note.notepad.ui.editor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.note.notepad.R
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.common.helpers.DialogHelpers
import com.note.notepad.databinding.ActivityCreateNoteBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateNoteActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityCreateNoteBinding::inflate)
    private val viewModel: CreateNoteViewModel by viewModel()
    private lateinit var undoNotes: UndoRedoManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        initListener()
        observeData()


    }

    private fun initView() {
        setSupportActionBar(binding.tbEditor)
        val noteId = intent.getIntExtra("EXTRA_NOTE_ID", -1)
        viewModel.loadData(noteId)
        undoNotes = UndoRedoManager(binding.edtContent)
    }

    private fun initListener() {
        binding.tbEditor.setNavigationOnClickListener {
            autoSave()
            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.currentNote.collect { note ->
                note?.let {
                    if (binding.edtTitle.text.isEmpty() && binding.edtContent.text.isEmpty()) {
                        val displayTitle = if(it.title== "Untitled") "" else it.title
                        binding.edtTitle.setText(displayTitle)
                        binding.edtContent.setText(it.content)
                        undoNotes.saveCheckpoints()
                    }
                }
            }
        }
    }

    private fun autoSave() {
        val newTitle = binding.edtTitle.text.toString()
        val newContent = binding.edtContent.text.toString()

        viewModel.saveNote(newTitle, newContent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_appbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_save -> {
                autoSave()
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.menu_undo -> {
                undoNotes.undo()
                true
            }
            R.id.menu_redo -> {
                undoNotes.redo()
                true
            }
            R.id.menu_undo_all -> {
                DialogHelpers.undoAllDialog(this) {
                    undoNotes.undoAll()
                }
                true
            }
            R.id.menu_delete -> {
                val currentTitle = binding.edtTitle.text.toString()
                DialogHelpers.deleteDialog(this, currentTitle) {
                    viewModel.deleteNote()
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}