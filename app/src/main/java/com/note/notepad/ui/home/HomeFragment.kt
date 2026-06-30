package com.note.notepad.ui.home

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.note.notepad.R
import com.note.notepad.common.base.BaseFragment
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.common.helpers.ColorHelpers
import com.note.notepad.common.helpers.DialogHelpers
import com.note.notepad.databinding.FragmentHomeBinding
import com.note.notepad.ui.editor.CreateNoteActivity
import com.note.notepad.ui.main.MainViewModel
import com.note.notepad.ui.main.adapter.MainAdapter
import com.note.notepad.utils.AppConstant
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {
    override val binding by viewBinding(FragmentHomeBinding::bind)
    private val viewModel: MainViewModel by activityViewModel()
    private lateinit var noteAdapter: MainAdapter
    private var currentSortOption = 0

    override fun initViews() {
        setupMenu()

        noteAdapter = MainAdapter(
            onItemClick = { noteId -> openEditorScreen(noteId = noteId, categoryId = -1) },
            onItemLongClick = { noteId -> viewModel.onSelection(noteId) }
        )
        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = noteAdapter
            itemAnimator = null
        }
    }

    override fun initListener() {
        binding.btnAddNote.setOnClickListener { viewModel.onFabClicked() }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (viewModel.isSelectionMode.value) {
                        viewModel.exitSelectionMode()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    override fun observeData() {
        viewModel.updateColorOrder(ColorHelpers.getMainColors(requireContext()))
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.noteList.collect { list ->
                noteAdapter.submitList(list) { binding.rvHome.scrollToPosition(0) }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sortOption.collect { option ->
                noteAdapter.updateSortOption(option)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.searchQuery.collect { query ->
                noteAdapter.updateSearchQuery(query)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.navigateToEdit.collect { targetCategoryId ->
                openEditorScreen(
                    noteId = -1,
                    categoryId = targetCategoryId
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isSelectionMode.collect { isMode ->
                noteAdapter.isSelectionMode = isMode
                binding.btnAddNote.isGone = isMode
                updateToolbar(isMode)
                requireActivity().invalidateOptionsMenu()
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
            combine(viewModel.categoryId, viewModel.categories) { id, list ->
                Pair(id, list)
            }.collect { (currentId, categoryList) ->
                val activity = (requireActivity() as? AppCompatActivity)
                activity?.supportActionBar?.title = getString(R.string.app_name)

                when (currentId) {
                    -1 -> {
                        activity?.supportActionBar?.subtitle = null
                    }

                    -2 -> {
                        activity?.supportActionBar?.subtitle =
                            getString(R.string.subtitle_uncategorized)
                    }

                    else -> {
                        if (categoryList.isNotEmpty()) {
                            val categoryName = categoryList.find { it.id == currentId }?.name
                            activity?.supportActionBar?.subtitle = categoryName
                        }
                    }
                }
            }
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_appbar, menu)
                setupSearchView(menu)
            }

            override fun onPrepareMenu(menu: Menu) {
                val isMode = viewModel.isSelectionMode.value
                menu.findItem(R.id.menu_select_all)?.isVisible = isMode
                menu.findItem(R.id.menu_delete)?.isVisible = isMode
                menu.findItem(R.id.menu_categorize)?.isVisible = isMode
                menu.findItem(R.id.menu_colorize)?.isVisible = isMode
                menu.findItem(R.id.menu_sort)?.isVisible = !isMode
                menu.findItem(R.id.menu_search)?.isVisible = !isMode
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_delete -> {
                        DialogHelpers.showConfirmDeleteDialog(requireContext()) { viewModel.deleteSelectionNotes() }
                        true
                    }

                    R.id.menu_select_all -> {
                        viewModel.selectAll(); true
                    }

                    R.id.menu_select_all_notes -> {
                        viewModel.selectAllNotes(); true
                    }

                    R.id.menu_categorize -> {
                        DialogHelpers.showCategorizeDialog(
                            requireContext(),
                            viewModel.categories.value
                        ) { selectedIds ->
                            viewModel.categorizeSelectedNotes(selectedIds)
                        }
                        true
                    }

                    R.id.menu_sort -> {
                        DialogHelpers.sortNotesDialog(
                            requireContext(),
                            currentSortOption
                        ) { option ->
                            currentSortOption = option
                            viewModel.sortNotes(option)
                        }
                        true
                    }

                    R.id.menu_colorize -> {
                        val firstId = viewModel.selectedIds.value.firstOrNull()
                        val currentColor = viewModel.noteList.value.find{it.note.id == firstId}?.note?.color?:0
                        DialogHelpers.showColorDialog(
                            requireContext(), currentColor) { color ->
                            viewModel.colorizeSelectedNotes(color)
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.menu_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchNotes(newText.orEmpty())
                return true
            }
        })
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
        val title =
            if (isMode) viewModel.selectedIds.value.size.toString() else getString(R.string.app_name)
        updateTitle(title)
    }

    private fun updateTitle(title: String) {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = title
    }

    private fun openEditorScreen(noteId: Int, categoryId: Int) {
        val intent = Intent(requireContext(), CreateNoteActivity::class.java).apply {
            putExtra(AppConstant.EXTRA_NOTE_ID, noteId)
            putExtra(AppConstant.SELECTED_CATEGORY_ID, categoryId)
            putExtra(AppConstant.EXTRA_SEARCH_QUERY, viewModel.searchQuery.value)
        }
        startActivity(intent)
    }

}