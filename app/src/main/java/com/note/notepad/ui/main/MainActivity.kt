package com.note.notepad.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View

import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.note.notepad.R
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.common.helpers.DialogHelpers
import com.note.notepad.databinding.ActivityMainBinding
import com.note.notepad.ui.editor.CreateNoteActivity
import com.note.notepad.ui.main.adapter.MainAdapter
import com.note.notepad.ui.trash.TrashActivity
import com.note.notepad.utils.AppConstant
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel: MainViewModel by viewModel()
    private lateinit var noteAdapter: MainAdapter
    private var currentSortOption = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBar.left, systemBar.top, systemBar.right, systemBar.bottom)
            insets
        }

        initView()
        observeData()
    }

    private fun initView() {
        setSupportActionBar(binding.tbMain)
        noteAdapter = MainAdapter(
            onItemClick = { noteId ->
                openEditorScreen(noteId)
            },
            onItemLongClick = { noteId ->
                viewModel.onSelection(noteId)
            }
        )
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        binding.rvMain.adapter = noteAdapter
        binding.rvMain.itemAnimator = null

        binding.btnAddNote.setOnClickListener {
            viewModel.onFabClicked()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val searchItem = binding.tbMain.menu?.findItem(R.id.menu_search)
                when {
                    viewModel.isSelectionMode.value -> {
                        viewModel.clearSelection()
                        viewModel.exitSelectionMode()
                    }
                    searchItem?.isActionViewExpanded == true -> {
                        searchItem.collapseActionView()
                    }
                    else -> {
                        finish()
                    }
                }
            }
        })

        initDrawer()
    }

    private fun openEditorScreen(noteId: Int) {
        val intent = Intent(this, CreateNoteActivity::class.java)
        intent.putExtra(AppConstant.EXTRA_NOTE_ID, noteId)
        intent.putExtra(AppConstant.EXTRA_SEARCH_QUERY, viewModel.searchQuery.value)
        startActivity(intent)
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.noteList.collect { list ->
                noteAdapter.submitList(list) {
                    binding.rvMain.scrollToPosition(0)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.navigateToEdit.collect { newNoteId ->
                openEditorScreen(newNoteId)
            }
        }
        lifecycleScope.launch {
            viewModel.selectedIds.collect { ids ->
                noteAdapter.updateSelection(ids)
                if (viewModel.isSelectionMode.value) {
                    supportActionBar?.title = ids.size.toString()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.isSelectionMode.collect { isMode ->
                noteAdapter.isSelectionMode = isMode
                updateTbForSelection(isMode)
                invalidateOptionsMenu()
            }
        }
        lifecycleScope.launch {
            viewModel.searchQuery.collect { query ->
                noteAdapter.updateSearchQuery(query)
            }
        }
        lifecycleScope.launch {
            viewModel.sortOption.collect { option ->
                noteAdapter.updateSortOption(option)
            }
        }
    }

    private fun updateTbForSelection(isMode: Boolean) {
        if (isMode) {
            binding.tbMain.title = viewModel.selectedIds.value.size.toString()
            binding.tbMain.setNavigationIcon(R.drawable.ic_back)
            binding.tbMain.setNavigationOnClickListener {
                viewModel.clearSelection()
                viewModel.exitSelectionMode()
            }
            binding.btnAddNote.visibility = View.GONE
        } else {
            binding.tbMain.setTitle(R.string.app_name)
            binding.tbMain.setNavigationIcon(R.drawable.ic_menu)
            binding.tbMain.setNavigationOnClickListener {
                binding.dlMain.open()
            }
            binding.btnAddNote.visibility = View.VISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_appbar, menu)
        val searchItem = menu?.findItem(R.id.menu_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setIconifiedByDefault(true)

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchNotes(newText.orEmpty())
                return true
            }
        })
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val isMode = viewModel.isSelectionMode.value
        menu?.findItem(R.id.menu_select_all)?.isVisible = isMode
        menu?.findItem(R.id.menu_delete)?.isVisible = isMode
        menu?.findItem(R.id.menu_search)?.isVisible = !isMode
        menu?.findItem(R.id.menu_sort)?.isVisible = !isMode
        menu?.findItem(R.id.menu_select_all_notes)?.isVisible = !isMode

        val searchItem = menu?.findItem(R.id.menu_search)
        val sortItem = menu?.findItem(R.id.menu_sort)

        searchItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                sortItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
                return true
            }

            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                sortItem?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                return true
            }
        })

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                DialogHelpers.showConfirmDeleteDialog(this) {
                    viewModel.deleteSelectionNotes()
                }
                true
            }

            R.id.menu_select_all -> {
                viewModel.selectAll()
                true
            }

            R.id.menu_select_all_notes -> {
                viewModel.selectAllNotes()
                true
            }

            R.id.menu_sort -> {
                sortNotesDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initDrawer() {
        binding.navMain.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.itTrash -> {
                    startActivity(Intent(this@MainActivity, TrashActivity::class.java))
                }

                R.id.itNote -> {
                    binding.dlMain.close()
                }
            }
            binding.dlMain.close()
            true
        }
    }

    private fun sortNotesDialog() {
        val options = arrayOf(
            "edit date: from newest",
            "edit date: from oldest",
            "title: A to Z",
            "title: Z to A",
            "creation date: from newest",
            "creation date: from oldest"
        )
        MaterialAlertDialogBuilder(this)
            .setTitle("Sort by")
            .setSingleChoiceItems(options, currentSortOption) { _, option ->
                currentSortOption = option
            }
            .setPositiveButton("SORT") { dialog, _ ->
                viewModel.sortNotes(currentSortOption)
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}
