package com.note.notepad.ui.editor

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
    private lateinit var searchManager: SearchManager
    private var isSearchMode = false
    private var toolBarMenu: Menu ?= null
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
        searchManager = SearchManager(binding.edtContent)
    }

    private fun initListener() {
        binding.tbEditor.setNavigationOnClickListener {
            if (isSearchMode) {
                toolBarMenu?.findItem(R.id.menu_search_editor)?.collapseActionView()
            } else {
                autoSave()
                Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show()
                onBackPressedDispatcher.onBackPressed()
            }
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
        toolBarMenu = menu
        val searchItem = menu?.findItem(R.id.menu_search_editor)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                searchManager.onSearch(newText.orEmpty()) {countText ->
                    toolBarMenu?.findItem(R.id.menu_search_count)?.title = countText
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

        })
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menu_search_down)?.isVisible = isSearchMode
        menu?.findItem(R.id.menu_search_editor)?.isVisible = isSearchMode
        menu?.findItem(R.id.menu_search_trigger)?.isVisible = !isSearchMode
        menu?.findItem(R.id.menu_search_up)?.isVisible = isSearchMode
        menu?.findItem(R.id.menu_search_count)?.isVisible = isSearchMode

//        menu?.findItem(R.id.menu_save)?.isVisible = !isSearchMode
//        menu?.findItem(R.id.menu_undo)?.isVisible = !isSearchMode
//        menu?.findItem(R.id.menu_redo)?.isVisible = !isSearchMode
//        menu?.findItem(R.id.menu_undo_all)?.isVisible = !isSearchMode
//        menu?.findItem(R.id.menu_delete)?.isVisible = !isSearchMode

        val searchItem = menu?.findItem(R.id.menu_search_editor)
        val searchView = searchItem?.actionView as? SearchView
        val saveItem = menu?.findItem(R.id.menu_save)
        val undoItem = menu?.findItem(R.id.menu_undo)

        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                isSearchMode = false
                invalidateOptionsMenu()
                searchManager.clearSearch()
                saveItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                undoItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                isSearchMode = true
                saveItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                undoItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                searchView?.post {
                    searchView.onActionViewExpanded()
                    searchView.requestFocus()
                }
                return true
            }
        })

        return super.onPrepareOptionsMenu(menu)
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
            R.id.menu_search_trigger -> {
                isSearchMode = true
                invalidateOptionsMenu()
                binding.root.post {
                    toolBarMenu?.findItem(R.id.menu_search_editor)?.expandActionView()
                }
                true
            }
            R.id.menu_search_up -> {
                searchManager.navigateSearch(isForward = true) { countText ->
                    toolBarMenu?.findItem(R.id.menu_search_count)?.title = countText
                }
                true
            }
            R.id.menu_search_down -> {
                searchManager.navigateSearch(isForward = false) { countText ->
                    toolBarMenu?.findItem(R.id.menu_search_count)?.title = countText
                }
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