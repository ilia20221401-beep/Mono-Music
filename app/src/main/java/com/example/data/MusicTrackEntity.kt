package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_tracks")
data class MusicTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val prompt: String,
    val model: String, // "lyria-3-clip-preview" or "lyria-3-pro-preview"
    val genre: String,
    val bpm: Int,
    val rootKey: String,
    val scale: String,
    val timeSignature: String,
    val audioFilePath: String,
    val durationSeconds: Int,
    val lyrics: String? = null,
    val vocalStyle: String? = null,
    val stemsSummary: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)
