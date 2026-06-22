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
import com.note.notepad.utils.AppConstant
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateNoteActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityCreateNoteBinding::inflate)
    private val viewModel: CreateNoteViewModel by viewModel()
    private lateinit var undoNotes: UndoRedoManager
    private lateinit var searchManager: SearchManager
    private var isSearchMode = false
    private var toolBarMenu: Menu? = null
    private var pendingQuery = ""
    private var isNoteLoad = false
    private var lastSavedTitle = ""
    private var lastSavedContent = ""
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
        val noteId = intent.getIntExtra(AppConstant.EXTRA_NOTE_ID, -1)
        pendingQuery = intent.getStringExtra(AppConstant.EXTRA_SEARCH_QUERY) ?: ""

        viewModel.loadData(noteId)
        undoNotes = UndoRedoManager(binding.edtContent)
        searchManager = SearchManager(binding.edtContent)
    }

    private fun initListener() {
        binding.tbEditor.setNavigationOnClickListener {
            if (isSearchMode) {
                toolBarMenu?.findItem(R.id.menu_search_editor)?.collapseActionView()
            } else {
                val isSaved = autoSave()
                if (isSaved) {
                    Toast.makeText(this, getString(R.string.note_saved_toast), Toast.LENGTH_SHORT).show()
                }
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.currentNote.collect { note ->
                note?.let {
                    if (binding.edtTitle.text.isEmpty() && binding.edtContent.text.isEmpty()) {
                        val displayTitle = if (it.title == getString(R.string.title_untitled)) "" else it.title
                        binding.edtTitle.setText(displayTitle)
                        binding.edtContent.setText(it.content)
                        lastSavedTitle = displayTitle
                        lastSavedContent = it.content
                        undoNotes.saveCheckpoints()
                        isNoteLoad = true
                        checkAndTriggerSearch()
                    }
                }
            }
        }
    }

    private fun autoSave(): Boolean {
        val currentTitle = binding.edtTitle.text.toString()
        val currentContent = binding.edtContent.text.toString()

        if (currentTitle == lastSavedTitle && currentContent == lastSavedContent) return false

        viewModel.saveNote(currentTitle, currentContent)
        lastSavedTitle = currentTitle
        lastSavedContent = currentContent

        return true
    }

    private fun checkAndTriggerSearch() {
        if (isNoteLoad && pendingQuery.isNotEmpty() && toolBarMenu != null) {
            val searchItem = toolBarMenu?.findItem(R.id.menu_search_editor)
            val searchView = searchItem?.actionView as? SearchView

            binding.tbEditor.post {
                searchItem?.expandActionView()
                searchView?.setQuery(pendingQuery, false)
                searchView?.clearFocus()
                pendingQuery = ""
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_appbar, menu)
        toolBarMenu = menu
        val searchItem = menu?.findItem(R.id.menu_search_editor)
        val searchView = searchItem?.actionView as? SearchView

        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                isSearchMode = false
                menu.findItem(R.id.menu_search_down)?.isVisible = false
                menu.findItem(R.id.menu_search_up)?.isVisible = false
                menu.findItem(R.id.menu_search_count)?.isVisible = false

                menu.findItem(R.id.menu_save)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                menu.findItem(R.id.menu_undo)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                searchManager.clearSearch()
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                isSearchMode = true
                menu.findItem(R.id.menu_search_down)?.isVisible = true
                menu.findItem(R.id.menu_search_up)?.isVisible = true
                menu.findItem(R.id.menu_search_count)?.isVisible = true

                menu.findItem(R.id.menu_save)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                menu.findItem(R.id.menu_undo)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)

                searchView?.post {
                    searchView.onActionViewExpanded()
                    searchView.requestFocus()
                }
                return true
            }
        })

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                searchManager.onSearch(newText.orEmpty()) { countText ->
                    toolBarMenu?.findItem(R.id.menu_search_count)?.title = countText
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

        })
        checkAndTriggerSearch()
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.menu_search_down)?.isVisible = isSearchMode
        menu?.findItem(R.id.menu_search_editor)?.isVisible = isSearchMode
        menu?.findItem(R.id.menu_search_trigger)?.isVisible = !isSearchMode
        menu?.findItem(R.id.menu_search_up)?.isVisible = isSearchMode
        menu?.findItem(R.id.menu_search_count)?.isVisible = isSearchMode

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                val isSaved = autoSave()
                if (isSaved) {
                    Toast.makeText(this, getString(R.string.note_saved_toast), Toast.LENGTH_SHORT).show()
                }
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