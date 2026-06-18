package com.note.notepad.ui.trash

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
import com.note.notepad.data.local.model.NoteItems
import com.note.notepad.databinding.ActivityTrashBinding
import com.note.notepad.ui.main.MainActivity
import com.note.notepad.ui.main.adapter.MainAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class TrashActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityTrashBinding::inflate)
    private val viewModel: TrashViewModel by viewModel()
    private lateinit var noteAdapter: MainAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
        observeData()
    }

    private fun initView() {
        setSupportActionBar(binding.tbTrash)
        noteAdapter = MainAdapter(
            onItemClick = { noteId ->
                if (viewModel.isSelectionMode.value) {
                    viewModel.onSelection(noteId)
                } else {
                    val note = viewModel.deleteList.value.find { it.id == noteId }
                    note?.let { showItemAction(it) }
                }
            },
            onItemLongClick = { noteId ->
                viewModel.onSelection(noteId)
            }
        )
        binding.rvTrash.layoutManager = LinearLayoutManager(this)
        binding.rvTrash.adapter = noteAdapter
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

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.deleteList.collect { list ->
                noteAdapter.submitList(list)
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
            binding.tbTrash.title = viewModel.selectedIds.value.size.toString()
            binding.tbTrash.setNavigationIcon(R.drawable.ic_back)
            binding.tbTrash.setNavigationOnClickListener {
                viewModel.clearSelection()
                viewModel.exitSelectionMode()
            }
        } else {
            binding.tbTrash.setTitle(R.string.app_name)
            binding.tbTrash.setNavigationIcon(R.drawable.ic_menu)
            binding.tbTrash.setNavigationOnClickListener {
                binding.dlTrash.open()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.trash_appbar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val isMode = viewModel.isSelectionMode.value
        menu?.findItem(R.id.menu_select_all)?.isVisible = isMode
        menu?.findItem(R.id.menu_delete)?.isVisible = isMode
        menu?.findItem(R.id.menu_selected_delete)?.isVisible = isMode
        menu?.findItem(R.id.menu_undelete_all)?.isVisible = !isMode
        menu?.findItem(R.id.menu_empty_trash)?.isVisible = !isMode
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewModel.restoreItem()
                true
            }

            R.id.menu_select_all -> {
                viewModel.selectAll()
                true
            }
            R.id.menu_empty_trash -> {
                clearTrashDialog()
                true
            }
            R.id.menu_undelete_all -> {
                undeleteAllDialog()
                true
            }
            R.id.menu_selected_delete -> {
                deleteSelectedDialog()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initDrawer() {
        binding.navTrash.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.itNote -> {
                    startActivity(Intent(this@TrashActivity, MainActivity::class.java))
                }

                R.id.itTrash -> {
                    binding.dlTrash.close()
                }
            }
            binding.dlTrash.close()
            true
        }
    }

    private fun showItemAction(note: NoteItems) {
        val options = arrayOf("Undelete", "Delete")
        var selectedOption = 0
        MaterialAlertDialogBuilder(this)
            .setTitle("Select an action for the note:")
            .setSingleChoiceItems(options, selectedOption) { _, option ->
                selectedOption = option
            }
            .setPositiveButton("OK") { dialog, _ ->
                if (selectedOption == 0) {
                    viewModel.restoreItem(note.id)
                } else {
                    viewModel.hardDelete(note.id)
                }
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun clearTrashDialog() {
        val message = "All trashed notes will be deleted permanently. Are you sure that you want to delete all of the trashed notes?"
        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton("YES") { dialog, _ ->
                viewModel.clearTrash()
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun undeleteAllDialog() {
        val message = "Restore all notes?"
        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton("YES") { dialog, _ ->
                viewModel.restoreAllTrash()
                dialog.dismiss()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteSelectedDialog() {
        val message = "Are you sure that you want to delete the selected notes? The notes will be deleted permanently."
        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                viewModel.hardDelete()
                dialog.dismiss()
            }
            .setNegativeButton("CANCEL") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}
