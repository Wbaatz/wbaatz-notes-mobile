package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_progress")
data class NoteProgress(
    @PrimaryKey val noteId: String,
    val isBookmarked: Boolean = false,
    val isCompleted: Boolean = false,
    val lastPageRead: Int = 1,
    val userNotes: String = "",
    val unlockedPremium: Boolean = false
)
