package com.note.notepad.ui.category

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.note.notepad.R
import com.note.notepad.common.base.BaseActivity
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.common.extension.hideKeyboard
import com.note.notepad.common.helpers.DialogHelpers
import com.note.notepad.data.local.model.CategoryItems
import com.note.notepad.databinding.ActivityCategoryEditorBinding
import com.note.notepad.ui.category.adapter.CategoryAdapter
import com.note.notepad.ui.main.MainActivity
import com.note.notepad.ui.trash.TrashActivity
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Collections

class CategoryEditorActivity : BaseActivity<ActivityCategoryEditorBinding>() {
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel: CategoryEditorViewModel by viewModel()
    private var mutableList = mutableListOf<CategoryItems>()

    override val binding by viewBinding(
        ActivityCategoryEditorBinding::inflate
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(binding.clCategory) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun initViews() {
        setSupportActionBar(binding.tbCategory)
        categoryAdapter = CategoryAdapter(
            onEditClick = { category ->
                DialogHelpers.showEditDialog(this, category.name) { newName ->
                    viewModel.editCategory(category, newName)
                }
            },
            onDeleteClick = { category ->
                DialogHelpers.deleteCategoryDialog(this, category.name) {
                    viewModel.deleteCategory(category)
                }
            }
        )
        binding.rvCategory.adapter = categoryAdapter
        binding.rvCategory.layoutManager = LinearLayoutManager(this)

        initDrawer()
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = viewHolder.adapterPosition
                val toPos = target.adapterPosition
                val newList = categoryAdapter.currentList.toMutableList()
                Collections.swap(newList, fromPos, toPos)
                categoryAdapter.submitList(newList)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewModel.updateCategoryPositions(categoryAdapter.currentList)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvCategory)
    }

    override fun initListener() {
        binding.btnAddCate.setOnClickListener {
            val categoryName = binding.edtAddCate.text.toString()
            viewModel.addCategory(categoryName)
            binding.edtAddCate.text?.clear()
            binding.edtAddCate.hideKeyboard()

        }
        binding.tbCategory.setNavigationOnClickListener {
            binding.dlCategory.open()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.cateList.collect { list ->
                categoryAdapter.submitList(list)
                updateDrawerCategory(list)
            }
        }

    }

    private fun initDrawer() {
        binding.navCategory.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.itNote -> {
                    binding.dlCategory.close()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)

                }
                R.id.itEditCategory -> {
                    binding.dlCategory.close()
                }
                R.id.itTrash -> {
                    binding.dlCategory.close()
                    val intent = Intent(this, TrashActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                }
            }
            true
        }
    }

    private fun updateDrawerCategory(categories: List<CategoryItems>) {
        val menu = binding.navCategory.menu

        val categoriesParent = menu.findItem(R.id.itCategoriesParent)
        val subMenu = categoriesParent.subMenu ?: return

        subMenu.removeGroup(R.id.grItemCategory)
        subMenu.findItem(R.id.itUncategorized)?.isVisible = categories.isNotEmpty()

        categories.forEachIndexed { index, category ->
            subMenu.add(R.id.grItemCategory, category.id, index, category.name).apply {
                icon = ContextCompat.getDrawable(this@CategoryEditorActivity, R.drawable.ic_category)

                setOnMenuItemClickListener {
                    binding.dlCategory.close()
                    true
                }
            }
        }
    }
}
