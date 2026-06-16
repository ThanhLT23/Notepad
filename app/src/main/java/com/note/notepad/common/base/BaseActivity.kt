package com.note.notepad.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding, VM: ViewModel>: AppCompatActivity() {
    abstract val binding: VB
    abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        initListener()
        observeViewModel()
    }

    abstract fun initViews()
    open fun initListener(){}
    abstract fun observeViewModel()

}