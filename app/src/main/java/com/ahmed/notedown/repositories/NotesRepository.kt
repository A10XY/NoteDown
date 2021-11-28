package com.ahmed.notedown.repositories

import androidx.lifecycle.LiveData
import com.ahmed.notedown.models.entities.Note
import com.ahmed.notedown.utils.NotesDao

class NotesRepository(private val notesDao: NotesDao) {
    val getAllNotes: LiveData<List<Note>> = notesDao.getAllNotes()

    suspend fun addNote(note: Note){
        notesDao.insertNewNote(note)
    }

    suspend fun deleteNote(note: Note){
        notesDao.deleteNote(note)
    }
}