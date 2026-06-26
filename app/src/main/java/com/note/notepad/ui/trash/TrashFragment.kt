package com.note.notepad.ui.trash

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.note.notepad.R
import com.note.notepad.common.base.BaseFragment
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.common.enums.NoteAction
import com.note.notepad.common.helpers.DialogHelpers
import com.note.notepad.databinding.FragmentTrashBinding
import com.note.notepad.ui.main.adapter.MainAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrashFragment : BaseFragment<FragmentTrashBinding>(R.layout.fragment_trash) {
    override val binding by viewBinding(FragmentTrashBinding::bind)
    private val viewModel: TrashViewModel by viewModel()
    private lateinit var noteAdapter: MainAdapter

    override fun initViews() {
        initMenu()
        noteAdapter = MainAdapter(
            onItemClick = { noteId ->
                if (viewModel.isSelectionMode.value) {
                    viewModel.onSelection(noteId)
                } else {
                    val note = viewModel.deleteList.value.find { it.note.id == noteId }
                    note?.let {
                        DialogHelpers.showItemAction(requireContext()) { action ->
                            when (action) {
                                NoteAction.RESTORE -> viewModel.restoreItem(noteId)
                                NoteAction.DELETE -> viewModel.hardDelete(noteId)
                            }
                        }
                    }
                }
            },
            onItemLongClick = { noteId ->
                viewModel.onSelection(noteId)
            },
            showCategory = false
        )
        binding.rvTrash.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
        }
    }

    override fun initListener() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.isSelectionMode.value) {
                        viewModel.exitSelectionMode()
                    } else {
                        if (!findNavController().popBackStack()) {
                            requireActivity().finish()
                        }
                    }
                }
            })
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.deleteList.collect { list ->
                noteAdapter.submitList(list)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedIds.collect { ids ->
                noteAdapter.updateSelection(ids)
                if (viewModel.isSelectionMode.value) {
                    updateTitle(ids.size.toString())
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSelectionMode.collect { isMode ->
                noteAdapter.isSelectionMode = isMode
                updateToolbar(isMode)
                requireActivity().invalidateOptionsMenu()
            }
        }
    }

    private fun initMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.trash_appbar, menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                val isMode = viewModel.isSelectionMode.value
                menu.findItem(R.id.menu_select_all)?.isVisible = isMode
                menu.findItem(R.id.menu_delete)?.isVisible = isMode
                menu.findItem(R.id.menu_selected_delete)?.isVisible = isMode
                menu.findItem(R.id.menu_undelete_all)?.isVisible = !isMode
                menu.findItem(R.id.menu_empty_trash)?.isVisible = !isMode
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_delete -> {
                        viewModel.restoreItem()
                        true
                    }

                    R.id.menu_select_all -> {
                        viewModel.selectAll()
                        true
                    }

                    R.id.menu_empty_trash -> {
                        DialogHelpers.clearTrashDialog(requireContext()) {
                            viewModel.clearTrash()
                        }
                        true
                    }

                    R.id.menu_undelete_all -> {
                        DialogHelpers.undeleteAllDialog(requireContext()) {
                            viewModel.restoreAllTrash()
                        }
                        true
                    }

                    R.id.menu_selected_delete -> {
                        DialogHelpers.deleteSelectedDialog(requireContext()) {
                            viewModel.hardDelete()
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun updateToolbar(isMode: Boolean) {
        val activity = requireActivity() as? AppCompatActivity
        val toolbar = activity?.findViewById<Toolbar>(R.id.tbMain)

        if (isMode) {
            toolbar?.setNavigationIcon(R.drawable.ic_back)
            toolbar?.setNavigationOnClickListener {
                viewModel.exitSelectionMode()
            }
        } else {
            val navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.navHostFragment) as? NavHostFragment
            val navController = navHostFragment?.navController
            val dlMain = activity?.findViewById<DrawerLayout>(R.id.dlMain)

            if (navController != null && dlMain != null) {
                val appBarConfiguration = AppBarConfiguration(
                    setOf(R.id.itNote, R.id.itTrash, R.id.itEditCategory),
                    dlMain
                )
                toolbar?.setupWithNavController(navController, appBarConfiguration)
            }
        }
        val title = if (isMode) {
            viewModel.selectedIds.value.size.toString()
        } else {
            getString(R.string.trash_title)
        }
        updateTitle(title)
    }

    private fun updateTitle(title: String) {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = title
    }
}