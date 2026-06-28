package com.example.ui.viewmodel

import android.app.Application
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.AdViewRequest
import com.example.data.api.NotesApiClient
import com.example.data.database.AppDatabase
import com.example.data.database.NoteProgress
import com.example.data.model.PdfNote
import com.example.data.repository.PdfNotesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).noteProgressDao()

    val noteProgressList: StateFlow<List<NoteProgress>> = dao.getAllProgress()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _notesList = MutableStateFlow<List<PdfNote>>(emptyList())
    val notesList: StateFlow<List<PdfNote>> = _notesList.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncError = MutableStateFlow<String?>(null)
    val syncError: StateFlow<String?> = _syncError.asStateFlow()

    private val fingerprint: String by lazy {
        Settings.Secure.getString(application.contentResolver, Settings.Secure.ANDROID_ID) ?: "android_device"
    }

    init {
        fetchNotesFromBackend()
    }

    fun fetchNotesFromBackend() {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncError.value = null
            try {
                val backendNotes = NotesApiClient.service.getNotes()
                val mapped = backendNotes.map { backendNote ->
                    PdfNotesRepository.mapBackendNoteToPdfNote(backendNote)
                }
                _notesList.value = mapped
            } catch (e: Exception) {
                Log.e("NotesViewModel", "Sync failed", e)
                _syncError.value = "Offline Mode: ${e.localizedMessage}"
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun loadNoteWithToken(noteId: String, onReady: (File) -> Unit) {
        viewModelScope.launch {
            _syncError.value = null
            _isSyncing.value = true
            try {
                Log.d("NotesViewModel", "[Token Flow] Phase 1: Registering Ad View")
                val tokenResp = withContext(Dispatchers.IO) {
                    NotesApiClient.service.registerAdView(
                        AdViewRequest(noteId = noteId, viewerFingerprint = fingerprint)
                    )
                }
                
                Log.d("NotesViewModel", "[Token Flow] Phase 2: Fetching note details")
                val detail = withContext(Dispatchers.IO) {
                    NotesApiClient.service.getNoteDetails(noteId, tokenResp.accessToken)
                }
                
                Log.d("NotesViewModel", "[Token Flow] Phase 3: Downloading PDF from ${detail.pdfUrl}")
                
                // USE HttpUrl to handle space encoding correctly
                val url = detail.pdfUrl.toHttpUrlOrNull()
                    ?.newBuilder()
                    ?.addQueryParameter("token", tokenResp.accessToken) // Try query param fallback
                    ?.build()
                    ?: throw Exception("Invalid PDF URL: ${detail.pdfUrl}")
                
                val request = Request.Builder()
                    .url(url)
                    .addHeader("x-access-token", tokenResp.accessToken)
                    .addHeader("viewer-fingerprint", fingerprint) // Add fingerprint just in case
                    .build()
                
                Log.d("NotesViewModel", "Outgoing Headers: x-access-token: ${tokenResp.accessToken}, viewer-fingerprint: $fingerprint")
                
                withContext(Dispatchers.IO) {
                    NotesApiClient.okHttpClient.newCall(request).execute().use { response ->
                        val bytes = response.body?.bytes()
                        if (response.isSuccessful && bytes != null) {
                            // Check for PDF magic bytes
                            val head = if (bytes.size >= 4) String(bytes.sliceArray(0..3)) else ""
                            if (!head.contains("%PDF")) {
                                val content = String(bytes)
                                Log.e("NotesViewModel", "Download failed: Not a PDF. Content: $content")
                                throw Exception("Server message: $content")
                            }
                            
                            val file = File(getApplication<Application>().cacheDir, "note_$noteId.pdf")
                            FileOutputStream(file).use { it.write(bytes) }
                            Log.d("NotesViewModel", "Download successful! Size: ${bytes.size} bytes")
                            withContext(Dispatchers.Main) {
                                onReady(file)
                            }
                        } else {
                            val errBody = String(bytes ?: byteArrayOf())
                            Log.e("NotesViewModel", "HTTP Error ${response.code}: $errBody")
                            throw Exception("Download error (${response.code})")
                        }
                    }
                }
                
            } catch (e: Exception) {
                Log.e("NotesViewModel", "CRITICAL ERROR", e)
                _syncError.value = e.localizedMessage
            } finally {
                _isSyncing.value = false
            }
        }
    }

    fun saveProgress(progress: NoteProgress) {
        viewModelScope.launch { dao.saveProgress(progress) }
    }

    fun toggleBookmark(noteId: String, isBookmarked: Boolean) {
        viewModelScope.launch {
            val progress = dao.getProgressForNoteSync(noteId)
            dao.saveProgress((progress ?: NoteProgress(noteId = noteId)).copy(isBookmarked = isBookmarked))
        }
    }
}
