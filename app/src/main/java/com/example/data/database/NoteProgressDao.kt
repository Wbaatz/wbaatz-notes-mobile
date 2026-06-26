package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteProgressDao {
    @Query("SELECT * FROM note_progress")
    fun getAllProgress(): Flow<List<NoteProgress>>

    @Query("SELECT * FROM note_progress WHERE noteId = :noteId LIMIT 1")
    fun getProgressForNote(noteId: String): Flow<NoteProgress?>

    @Query("SELECT * FROM note_progress WHERE noteId = :noteId LIMIT 1")
    suspend fun getProgressForNoteSync(noteId: String): NoteProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: NoteProgress)

    @Query("UPDATE note_progress SET isBookmarked = :isBookmarked WHERE noteId = :noteId")
    suspend fun updateBookmark(noteId: String, isBookmarked: Boolean)

    @Query("UPDATE note_progress SET unlockedPremium = 1 WHERE noteId = :noteId")
    suspend fun unlockPremiumNote(noteId: String)
}
