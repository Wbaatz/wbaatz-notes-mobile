package com.example.data.repository

import android.net.Uri
import com.example.data.api.BackendNote
import com.example.data.model.NotePage
import com.example.data.model.PdfNote

object PdfNotesRepository {
    val notes = listOf<PdfNote>() // REMOVED ALL HARDCODED DUMMY NOTES FROM REPOSITORY

    fun getNoteById(id: String): PdfNote? {
        return notes.find { it.id == id }
    }

    fun mapBackendNoteToPdfNote(backendNote: BackendNote): PdfNote {
        val title = backendNote.title
        val subject = backendNote.subject

        val mappedCategory = when {
            subject.contains("9618", ignoreCase = true) || subject.contains("A Level", ignoreCase = true) -> "A Level (9618)"
            subject.contains("2210", ignoreCase = true) || subject.contains("O Level", ignoreCase = true) -> "O Level (2210)"
            else -> "Computer Science"
        }

        val backendBaseUrl = "https://wbaatz-notes-backend.onrender.com" // Removed trailing slash
        
        // Properly encode the path to handle spaces and special characters
        val rawPath = backendNote.pdfPath ?: ""
        val fullPdfUrl = if (rawPath.startsWith("http")) {
            rawPath
        } else {
            // Ensure path doesn't start with leading slash to avoid double slashes
            val cleanPath = rawPath.removePrefix("/")
            val segments = cleanPath.split("/")
            val encodedPath = segments.joinToString("/") { Uri.encode(it) }
            "$backendBaseUrl/$encodedPath"
        }

        val fullThumbnailUrl = backendNote.thumbnailPath?.let { path ->
            if (path.startsWith("http")) path else {
                val cleanThumbPath = path.removePrefix("/")
                val encodedThumbPath = cleanThumbPath.split("/").joinToString("/") { Uri.encode(it) }
                "$backendBaseUrl/$encodedThumbPath"
            }
        }

        return PdfNote(
            id = backendNote.id,
            title = backendNote.title,
            description = backendNote.description?.takeIf { it.isNotBlank() } ?: "Live syllabus document for ${backendNote.title}. Fetched from wbaatz backend.",
            category = mappedCategory,
            pages = 0, // No dummy page count
            pdfUrl = fullPdfUrl,
            thumbnailUrl = fullThumbnailUrl,
            youtubeUrl = "https://www.youtube.com/results?search_query=${Uri.encode(backendNote.title)}",
            duration = "Live Document",
            contentPages = emptyList(), // STRICTLY EMPTY: No dummy data allowed
            isPremium = false,
            isLiveApi = true
        )
    }
}
