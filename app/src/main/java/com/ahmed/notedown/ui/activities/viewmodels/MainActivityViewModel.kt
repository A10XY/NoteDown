package com.ahmed.notedown.ui.activities.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.ahmed.notedown.databases.NotesDatabase
import com.ahmed.notedown.models.entities.Note
import com.ahmed.notedown.repositories.NotesRepository

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotesRepository
    private val getAllNotes: LiveData<List<Note>>

    init {
        val notesDao = NotesDatabase.getDatabase(application).notesDao()
        repository = NotesRepository(notesDao)
        getAllNotes = repository.getAllNotes
    }

    fun getAllNotes(): LiveData<List<Note>> {
        return getAllNotes
    }
}