package com.ahmed.notedown.utils

import androidx.lifecycle.LiveData
import androidx.room.*
import com.ahmed.notedown.models.entities.Note

@Dao
interface NotesDao {
    @Query("SELECT * FROM notes_table ORDER BY id DESC")
    fun getAllNotes(): LiveData<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}