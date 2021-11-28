package com.ahmed.notedown.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ahmed.notedown.adapters.NotesAdapter
import com.ahmed.notedown.databinding.ActivityMainBinding
import com.ahmed.notedown.ui.activities.viewmodels.MainActivityViewModel
import com.ahmed.notedown.ui.activities.viewmodels.factories.MainActivityViewModelFactory
import com.ahmed.notedown.utils.ContextUtils
import com.ahmed.notedown.utils.Preferencer

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val  binding get() = _binding!!

    private lateinit var mainActivityViewModel: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mainActivityViewModelFactory = MainActivityViewModelFactory(this.application)
        mainActivityViewModel = ViewModelProvider(this, mainActivityViewModelFactory)[MainActivityViewModel::class.java]
        getAllNotes()
        setListeners()
    }

    private fun setListeners() {
        binding.NewNoteFAB.setOnClickListener {
            Intent(this, CreateNoteActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.ToSettingsImageView.setOnClickListener {
            Intent(this, SettingsActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    private fun getAllNotes(){
        mainActivityViewModel.getAllNotes().observe(this, {
            if (it.isNotEmpty()){
                val adapter = NotesAdapter(it)
                binding.NotesRecyclerView.apply {
                    this.adapter = adapter
                    layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                }
                binding.NoNotesLayout.isVisible = false
            } else {
                binding.NoNotesLayout.isVisible = true
                binding.NoNotesTextView.alpha = 0f
                binding.NoNotesTextView.animate().apply {
                    duration = 1500
                    alpha(1f)
                }
            }
        })
    }

    override fun attachBaseContext(newBase: Context?) {
        val language = Preferencer(newBase!!).getString("LANGUAGE")
        super.attachBaseContext(ContextUtils.wrap(newBase, language))
    }
}