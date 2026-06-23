package com.note.notepad.ui.category

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.note.notepad.R
import com.note.notepad.common.base.BaseActivity
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.databinding.ActivityCategoryEditorBinding

class CategoryEditorActivity : BaseActivity<ActivityCategoryEditorBinding>() {

    override val binding by viewBinding(
        ActivityCategoryEditorBinding::inflate
    )


}