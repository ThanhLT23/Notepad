package com.note.notepad.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.note.notepad.R
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.databinding.ActivityMainBinding
import com.note.notepad.ui.editor.CreateNoteActivity
import com.note.notepad.ui.main.adapter.MainAdapter
import com.note.notepad.ui.trash.TrashActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel: MainViewModel by viewModel()
    private lateinit var noteAdapter: MainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBar.top, 0, 0)
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
        binding.fabAddNote.setOnClickListener {
            viewModel.onFabClicked()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isSelectionMode.value) {
                    viewModel.clearSelection()
                    viewModel.exitSelectionMode()
                } else {
                    finish()
                }
            }
        })

        initDrawer()
    }

    private fun openEditorScreen(noteId: Int) {
        val intent = Intent(this, CreateNoteActivity::class.java)
        intent.putExtra("EXTRA_NOTE_ID", noteId)
        startActivity(intent)
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.noteList.collect { list ->
                noteAdapter.submitList(list)
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
    }

    private fun updateTbForSelection(isMode: Boolean) {
        if (isMode) {
            binding.tbMain.title = viewModel.selectedIds.value.size.toString()
            binding.tbMain.setNavigationIcon(R.drawable.ic_back)
            binding.tbMain.setNavigationOnClickListener {
                viewModel.clearSelection()
                viewModel.exitSelectionMode()

            }
            binding.fabAddNote.hide()
        } else {
            binding.tbMain.setTitle(R.string.app_name)
            binding.tbMain.setNavigationIcon(R.drawable.ic_menu)
            binding.tbMain.setNavigationOnClickListener {
                binding.dlMain.open()
            }
            binding.fabAddNote.show()
        }
    }

    private fun showConfirmDeleteDialog() {
        val selectedCount = viewModel.selectedIds.value.size
        val message = if (selectedCount == 1) {
            "Delete the selected note?"
        } else {
            "Delete $selectedCount selected notes?"
        }
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                viewModel.deleteSelectionNotes()
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_appbar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val isMode = viewModel.isSelectionMode.value
        menu?.findItem(R.id.menu_select_all)?.isVisible = isMode
        menu?.findItem(R.id.menu_delete)?.isVisible = isMode
        menu?.findItem(R.id.menu_search)?.isVisible = !isMode
        menu?.findItem(R.id.menu_sort)?.isVisible = !isMode

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                showConfirmDeleteDialog()
                true
            }

            R.id.menu_select_all -> {
                viewModel.selectAll()
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
}
