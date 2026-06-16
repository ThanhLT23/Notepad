package com.note.notepad.ui.editor

import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.note.notepad.R
import com.note.notepad.common.delegate.viewBinding
import com.note.notepad.databinding.ActivityCreateNoteBinding

class CreateNoteActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityCreateNoteBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.tbEditor)
        binding.tbEditor.setNavigationOnClickListener {
            Toast.makeText(this, "Back", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.editor_appbar, menu)
        return true
    }
}