package com.ahmed.notedown.ui.activities.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ahmed.notedown.ui.activities.viewmodels.CreateNoteActivityViewModel

class CreateNoteActivityViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateNoteActivityViewModel::class.java)){
            return CreateNoteActivityViewModel(application) as T
        }
        throw IllegalArgumentException("Couldn't create class!")
    }
}