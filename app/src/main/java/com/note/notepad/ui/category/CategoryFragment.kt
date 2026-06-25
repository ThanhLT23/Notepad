package com.note.notepad.ui.category

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.note.notepad.R
import com.note.notepad.common.base.BaseFragment
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.common.extension.hideKeyboard
import com.note.notepad.common.helpers.DialogHelpers
import com.note.notepad.databinding.FragmentCategoryBinding
import com.note.notepad.ui.category.adapter.CategoryAdapter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Collections

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(R.layout.fragment_category) {
    override val binding by viewBinding(FragmentCategoryBinding::bind)
    private val viewModel: CategoryEditorViewModel by viewModel()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun initViews() {
        categoryAdapter = CategoryAdapter(
            onEditClick = { category ->
                DialogHelpers.showEditDialog(requireContext(), category.name) { newName ->
                    viewModel.editCategory(category, newName)
                }
            },
            onDeleteClick = { category ->
                DialogHelpers.deleteCategoryDialog(requireContext(), category.name) {
                    viewModel.deleteCategory(category)
                }
            }
        )
        binding.rvCategory.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        setupItemTouchHelper()
    }

    override fun initListener() {
        binding.btnAddCate.setOnClickListener {
            val name = binding.edtAddCate.text.toString()
            if (name.isNotBlank()) {
                viewModel.addCategory(name)
                binding.edtAddCate.text?.clear()
                binding.edtAddCate.hideKeyboard()
            }
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cateList.collect { list ->
                    categoryAdapter.submitList(list)
                }
            }
        }
    }

    private fun setupItemTouchHelper() {
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
        ) {
            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPos = vh.adapterPosition
                val toPos = target.adapterPosition
                val newList = categoryAdapter.currentList.toMutableList()
                Collections.swap(newList, fromPos, toPos)
                categoryAdapter.submitList(newList)
                return true
            }

            override fun onSwiped(vh: RecyclerView.ViewHolder, direction: Int) {}
            override fun clearView(rv: RecyclerView, vh: RecyclerView.ViewHolder) {
                super.clearView(rv, vh)
                viewModel.updateCategoryPositions(categoryAdapter.currentList)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.rvCategory)
    }

}