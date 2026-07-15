package com.naufal.cheddar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naufal.cheddar.data.Note
import com.naufal.cheddar.data.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class JournoteViewModel(private val dao: NoteDao) : ViewModel() {
    val allNotes = dao.getAllNotes().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Fetches a single note for the Editor
    fun getNoteById(id: Int): Flow<Note?> = dao.getNoteById(id)

    // Saves a new note OR updates an existing one if the ID is passed
    fun saveNote(id: Int = 0, title: String, content: String) {
        viewModelScope.launch { dao.insert(Note(id = id, title = title, content = content)) }
    }

    // Soft-deletes the note by moving it to the trash
    fun deleteNote(note: Note) {
        viewModelScope.launch { dao.update(note.copy(isTrashed = true)) }
    }
}