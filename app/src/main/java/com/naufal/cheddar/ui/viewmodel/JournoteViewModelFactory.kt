package com.naufal.cheddar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.naufal.cheddar.data.NoteDao

class JournoteViewModelFactory(private val dao: NoteDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JournoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JournoteViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}