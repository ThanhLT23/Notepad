package com.note.notepad.ui.main

import android.view.View
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
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
        binding.tbMain.setSubtitleTextColor(ContextCompat.getColor(this, R.color.white))
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.itNote, R.id.itTrash, R.id.itEditCategory),
            binding.dlMain
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.itNote) {
                supportActionBar?.subtitle = null
            }
        }
        binding.navMain.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.itNote -> {
                    viewModel.setCategory(-1)
                    if (navController.currentDestination?.id != R.id.itNote) {
                        navController.navigate((R.id.itNote))
                    }
                    binding.dlMain.close()
                    true
                }
                R.id.itUncategorized -> {
                    viewModel.setCategory(-2)
                    if (navController.currentDestination?.id != R.id.itNote) {
                        navController.navigate((R.id.itNote))
                    }
                    binding.dlMain.close()
                    true
                }
                else -> {
                    val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) binding.dlMain.close()
                    handled
                }
            }
        }
        binding.dlMain.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                for (i in 0 until binding.tbMain.childCount) {
                    val child = binding.tbMain.getChildAt(i)
                    if (child is ImageButton) {
                        child.rotation = slideOffset * 360f
                    }
                }
            }
        })
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
                viewModel.setCategory(category.id)
                navController.navigate(R.id.itNote)
                binding.dlMain.close()
                true
            }
        }
    }
}
}

