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

    // Soft delete: Moves to trash
    fun trashNote(note: Note) {
        viewModelScope.launch {
            dao.update(note.copy(isTrashed = true, trashedTimestamp = System.currentTimeMillis()))
        }
    }

    // Restores from trash
    fun restoreNote(note: Note) {
        viewModelScope.launch {
            dao.update(note.copy(isTrashed = false, trashedTimestamp = 0L))
        }
    }

    // Hard delete: Nukes it permanently
    fun deleteNoteForever(note: Note) {
        viewModelScope.launch {
            dao.delete(note)
        }
    }

    // Sends a note to the Archive
    fun archiveNote(note: Note) {
        viewModelScope.launch {
            dao.update(note.copy(isArchived = true))
        }
    }

    // Brings a note back to the main Entries screen
    fun unarchiveNote(note: Note) {
        viewModelScope.launch {
            dao.update(note.copy(isArchived = false))
        }
    }
}