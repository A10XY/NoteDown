package com.ahmed.notedown.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ahmed.notedown.databinding.NoteItemBinding
import com.ahmed.notedown.models.entities.Note

class NotesAdapter(private var notesList: List<Note>) : RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {
    inner class NotesViewHolder(binding: NoteItemBinding) : RecyclerView.ViewHolder(binding.root){
        val noteTitle = binding.NoteTitleTextView
        val noteText = binding.NoteTextTextView
        val noteDateTime = binding.NoteDateTimeTextView
        val noteImage = binding.NoteImageImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        val binding = NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        holder.apply {
            noteTitle.text = notesList[position].title
            noteDateTime.text = notesList[position].dateTime
            /*if (noteText.text.length > 10){
                noteText.text = noteslist[position].text + "..."
            }*/
            /*if (noteslist[position].imagePath != null){
                noteImage.setImageBitmap(decodeImage(position))
                noteImage.isVisible = true
            }*/
            val originalNoteText = notesList[position].text
            val shorterNoteText = originalNoteText.take(15)
            if (originalNoteText.length > 15){
                noteText.text = "$shorterNoteText.."
            } else {
                noteText.text = originalNoteText
            }
        }
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    private fun decodeImage(position: Int): Bitmap {
        val imagePath = notesList[position].imagePath!!
        val decodedByteArray = Base64.decode(imagePath, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
    }
}