package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.NotesApiClient
import com.example.data.database.AppDatabase
import com.example.data.database.NoteProgress
import com.example.data.model.PdfNote
import com.example.data.repository.PdfNotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).noteProgressDao()

    // Stream all progress persistently from Room DB
    val noteProgressList: StateFlow<List<NoteProgress>> = dao.getAllProgress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // State for all notes (starts with offline/cached, gets updated with live API notes)
    private val _notesList = MutableStateFlow<List<PdfNote>>(PdfNotesRepository.notes)
    val notesList: StateFlow<List<PdfNote>> = _notesList.asStateFlow()

    // Loading/Error states for API
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError.asStateFlow()

    init {
        fetchNotesFromBackend()
    }

    fun fetchNotesFromBackend() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncError.value = null
            try {
                val backendNotes = NotesApiClient.service.getNotes()
                if (backendNotes.isNotEmpty()) {
                    val mapped = backendNotes.map { backendNote ->
                        PdfNotesRepository.mapBackendNoteToPdfNote(backendNote)
                    }
                    _notesList.value = mapped
                    Log.d("NotesViewModel", "Successfully synced ${mapped.size} notes from backend API")
                }
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Failed to fetch live notes from backend", e)
                _syncError.value = e.localizedMessage ?: "Network connection error"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun saveProgress(progress: NoteProgress) {
        viewModelScope.launch {
            dao.saveProgress(progress)
        }
    }

    fun toggleBookmark(noteId: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            val progress = dao.getProgressForNoteSync(noteId)
            val updated = (progress ?: NoteProgress(noteId = noteId)).copy(
                isBookmarked = isBookmarked
            )
            dao.saveProgress(updated)
        }
    }

    fun unlockPremiumNote(noteId: String) {
        viewModelScope.launch {
            val progress = dao.getProgressForNoteSync(noteId)
            val updated = (progress ?: NoteProgress(noteId = noteId)).copy(
                unlockedPremium = true
            )
            dao.saveProgress(updated)
        }
    }
}
