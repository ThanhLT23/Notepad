package com.note.notepad.ui.category

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.note.notepad.common.base.BaseActivity
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.common.helpers.DialogHelpers
import com.note.notepad.databinding.ActivityCategoryEditorBinding
import com.note.notepad.ui.category.adapter.CategoryAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryEditorActivity : BaseActivity<ActivityCategoryEditorBinding>() {
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel: CategoryEditorViewModel by viewModel()

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
                viewModel.deleteCategory(category)
            }
        )
        binding.rvCategory.adapter = categoryAdapter
        binding.rvCategory.layoutManager = LinearLayoutManager(this)

    }

    override fun initListener() {
        binding.btnAddCate.setOnClickListener {
            val categoryName = binding.edtAddCate.text.toString()
            viewModel.addCategory(categoryName)
            binding.edtAddCate.text?.clear()
        }
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.cateList.collect { list ->
                categoryAdapter.submitList(list)
            }
        }
    }
}
