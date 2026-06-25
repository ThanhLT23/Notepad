package com.note.notepad.ui.main

import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.note.notepad.R
import com.note.notepad.common.base.BaseActivity
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.data.local.model.CategoryItems
import com.note.notepad.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {
    override val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel: MainViewModel by viewModel()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun initViews() {
        setSupportActionBar(binding.tbMain)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.itNote, R.id.itTrash, R.id.itEditCategory),
            binding.dlMain
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navMain.setupWithNavController(navController)
    }

    override fun observeData() {
        lifecycleScope.launch {
            viewModel.categories.collect { category ->
                updateDrawerCategory(category)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun updateDrawerCategory(categories: List<CategoryItems>) {
        val menu = binding.navMain.menu
        val categoriesParent = menu.findItem(R.id.itCategoriesParent)
        val menuSub = categoriesParent.subMenu ?: return

        menuSub.removeGroup(R.id.grItemCategory)
        menuSub.findItem(R.id.itUncategorized)?.isVisible = categories.isNotEmpty()

        categories.forEachIndexed { index, category ->
            menuSub.add(R.id.grItemCategory, category.id, index, category.name).apply {
                icon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_category)

                setOnMenuItemClickListener {
                    binding.dlMain.close()
                    true
                }
            }
        }
    }
}

