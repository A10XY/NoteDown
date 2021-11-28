package com.ahmed.notedown.ui.activities.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ahmed.notedown.ui.activities.viewmodels.MainActivityViewModel

@Suppress("UNCHECKED_CAST")
class MainActivityViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)){
            return MainActivityViewModel(application) as T
        }
        throw IllegalArgumentException("Couldn't create class")
    }
}