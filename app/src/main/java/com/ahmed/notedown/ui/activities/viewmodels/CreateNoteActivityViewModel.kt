package com.ahmed.notedown.ui.activities.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.ahmed.notedown.databases.NotesDatabase
import com.ahmed.notedown.models.entities.Note
import com.ahmed.notedown.repositories.NotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateNoteActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NotesRepository
    private val getAllNotes: LiveData<List<Note>>

    init {
        val notesDao = NotesDatabase.getDatabase(application).notesDao()
        repository = NotesRepository(notesDao)
        getAllNotes = repository.getAllNotes
    }

    fun addNote(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addNote(note)
        }
    }

    fun deleteNote(note: Note){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteNote(note)
        }
    }
}