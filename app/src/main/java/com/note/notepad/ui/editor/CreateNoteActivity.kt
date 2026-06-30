package com.note.notepad.ui.editor

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.note.notepad.R
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.common.extension.showToast
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
    private var categoryId = -1
    private var lastSavedCategoryIds: List<Int> = emptyList()
    private var lastSavedColor = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())
            val bottomInset = if (ime.bottom > 0) ime.bottom else systemBars.bottom
            v.setPadding(systemBars.left, 0, systemBars.right, bottomInset)
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
        categoryId = intent.getIntExtra(AppConstant.SELECTED_CATEGORY_ID, -1)

        viewModel.loadData(noteId, categoryId)
        if (noteId == -1) {
            lastSavedCategoryIds =
                if (categoryId != -1 && categoryId != -2) listOf(categoryId) else emptyList()
        }
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
                    Toast.makeText(this, getString(R.string.note_saved_toast), Toast.LENGTH_SHORT)
                        .show()
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
                        val displayTitle =
                            if (it.title == getString(R.string.title_untitled)) "" else it.title
                        binding.edtTitle.setText(displayTitle)
                        binding.edtContent.setText(it.content)
                        lastSavedTitle = displayTitle
                        lastSavedContent = it.content
                        lastSavedCategoryIds = viewModel.selectedCategoryIds.value
                        lastSavedColor = it.color
                        undoNotes.saveCheckpoints()
                    }
                    isNoteLoad = true
                    checkAndTriggerSearch()
                }
            }
        }
        lifecycleScope.launch {
            viewModel.allCategories.collect {}
        }
        lifecycleScope.launch {
            viewModel.noteColor.collect { color ->
                val isDefault = (color == 0)
                val displayColor = if (isDefault) {
                    ContextCompat.getColor(this@CreateNoteActivity, R.color.bg_text_input)
                } else {
                    color
                }
                val displayABColor = if (isDefault) {
                    ContextCompat.getColor(this@CreateNoteActivity, R.color.primaryColor)
                } else {
                    getAppBarColor(color)
                }
                val displayBGColor = if (isDefault) {
                    ContextCompat.getColor(this@CreateNoteActivity, R.color.backgroundColor)
                } else {
                    getAppBarColor(color)
                }

                val editorBg = binding.clEditor.background as? GradientDrawable
                editorBg?.setColor(displayColor)
                binding.tbEditor.setBackgroundColor(displayABColor)
                binding.root.setBackgroundColor(displayBGColor)
            }
        }
    }

    private val colorMapping = mapOf(
        R.color.color_light_peach_pink to R.color.bg_color_light_peach_pink,
        R.color.color_peach_orange to R.color.bg_color_peach_orange,
        R.color.color_pastel_yellow to R.color.bg_color_pastel_yellow,
        R.color.color_pastel_green to R.color.bg_color_pastel_green,
        R.color.color_pastel_aqua to R.color.bg_color_pastel_aqua,
        R.color.color_soft_blue to R.color.bg_color_soft_blue,
        R.color.color_soft_purple to R.color.bg_color_soft_purple,
        R.color.color_pastel_pink to R.color.bg_color_pastel_pink,
        R.color.color_off_white to R.color.bg_color_off_white,
        R.color.color_pastel_blue to R.color.bg_color_pastel_blue,
        R.color.color_light_aqua to R.color.bg_color_light_aqua,
        R.color.color_cream_white to R.color.bg_color_cream_white,
        R.color.color_vanilla to R.color.bg_color_vanilla,
        R.color.color_soft_rose to R.color.bg_color_soft_rose,
        R.color.color_light_lavender to R.color.bg_color_light_lavender,
        R.color.color_muted_blue to R.color.bg_color_light_muted_blue,
        R.color.color_mist_blue to R.color.bg_color_light_mist_blue,
        R.color.color_mint_aqua to R.color.bg_color_light_mint_aqua,
        R.color.color_mist_green to R.color.bg_color_mist_green,
        R.color.color_dusty_rose to R.color.bg_color_dusty_rose,
        R.color.color_plum_purple to R.color.bg_color_plum_purple,
        R.color.color_berry_pink to R.color.bg_color_berry_pink,
        R.color.color_coral_red to R.color.bg_color_coral_red,
        R.color.color_coral_orange to R.color.bg_color_peach_orange,
        R.color.color_golden_yellow to R.color.bg_color_golden_yellow,
        R.color.color_honey_cream to R.color.bg_color_honey_cream
    )

    private val actualColorMap by lazy {
        colorMapping.map { (keyRes, valRes) ->
            val actualKeyColor = ContextCompat.getColor(this, keyRes)
            val actualValueColor = ContextCompat.getColor(this, valRes)
            actualKeyColor to actualValueColor
        }.toMap()
    }

    private fun getAppBarColor(noteColor: Int): Int {
        return actualColorMap[noteColor] ?: ContextCompat.getColor(this, R.color.primaryColor)

    }

    private fun autoSave(): Boolean {
        val currentTitle = binding.edtTitle.text.toString()
        val currentContent = binding.edtContent.text.toString()
        val currentCategories = viewModel.selectedCategoryIds.value
        val currentColor = viewModel.noteColor.value

        if (currentTitle == lastSavedTitle &&
            currentContent == lastSavedContent &&
            currentCategories == lastSavedCategoryIds &&
            currentColor == lastSavedColor
        ) return false

        viewModel.saveNote(currentTitle, currentContent)
        lastSavedTitle = currentTitle
        lastSavedContent = currentContent
        lastSavedCategoryIds = currentCategories
        lastSavedColor = currentColor

        return true
    }

    private fun checkAndTriggerSearch() {
        if (!isNoteLoad || pendingQuery.isEmpty() || toolBarMenu == null) return
        val searchItem = toolBarMenu?.findItem(R.id.menu_search_editor)
        val searchView = searchItem?.actionView as? SearchView

        searchItem?.isVisible = true
        toolBarMenu?.findItem(R.id.menu_search_trigger)?.isVisible = false

        binding.tbEditor.post {
            searchItem?.expandActionView()
            searchView?.setQuery(pendingQuery, false)
            searchView?.clearFocus()
            pendingQuery = ""
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
        menu?.findItem(R.id.menu_search_count)?.isVisible = isSearchMode
        menu?.findItem(R.id.menu_search_trigger)?.isVisible = !isSearchMode
        menu?.findItem(R.id.menu_search_up)?.isVisible = isSearchMode

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_save -> {
                val isSaved = autoSave()
                if (isSaved) {
                    Toast.makeText(this, getString(R.string.note_saved_toast), Toast.LENGTH_SHORT)
                        .show()
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
                val searchItem = toolBarMenu?.findItem(R.id.menu_search_editor)
                searchItem?.isVisible = true
                item.isVisible = false
                binding.root.post {
                    searchItem?.expandActionView()
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

            R.id.menu_editor_categorize -> {
                DialogHelpers.showCategorizeDialog(
                    this,
                    viewModel.allCategories.value,
                    viewModel.selectedCategoryIds.value
                ) { selectedIds ->
                    viewModel.updateSelectedCategories(selectedIds)
                    if (autoSave()) {
                        showToast(R.string.category_updated_toast)
                    }
                }
                true
            }

            R.id.menu_editor_colorize -> {
                DialogHelpers.showColorDialog(this, viewModel.noteColor.value) { color ->
                    viewModel.updateNoteColor(color)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}