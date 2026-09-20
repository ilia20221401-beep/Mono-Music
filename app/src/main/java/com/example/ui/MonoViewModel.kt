package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.MonoAudioPlayer
import com.example.data.MusicDatabase
import com.example.data.MusicRepository
import com.example.data.MusicTrackEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MonoViewModel(application: Application) : AndroidViewModel(application) {

    private val database = MusicDatabase.getInstance(application)
    val repository = MusicRepository(application, database.musicTrackDao())
    val audioPlayer = MonoAudioPlayer(application)

    val allTracks: StateFlow<List<MusicTrackEntity>> = repository.allTracks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _lastGeneratedTrack = MutableStateFlow<MusicTrackEntity?>(null)
    val lastGeneratedTrack: StateFlow<MusicTrackEntity?> = _lastGeneratedTrack.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialTracksIfEmpty()
        }
    }

    fun generateMusic(
        modelName: String,
        prompt: String,
        genre: String,
        bpm: Int,
        rootKey: String,
        scale: String,
        timeSignature: String,
        includeVocals: Boolean,
        vocalStyle: String?,
        customLyrics: String?,
        stemsConfig: String
    ) {
        if (_isGenerating.value) return
        viewModelScope.launch {
            _isGenerating.value = true
            val result = repository.generateTrack(
                modelName = modelName,
                prompt = prompt,
                genre = genre,
                bpm = bpm,
                rootKey = rootKey,
                scale = scale,
                timeSignature = timeSignature,
                includeVocals = includeVocals,
                vocalStyle = vocalStyle,
                customLyrics = customLyrics,
                stemsConfig = stemsConfig
            )

            result.onSuccess { track ->
                _lastGeneratedTrack.value = track
                // Automatically load and play newly generated track
                audioPlayer.playTrack(track)
                _userMessage.value = "Track '${track.title}' generated successfully!"
            }.onFailure { error ->
                _userMessage.value = "Generation notice: ${error.localizedMessage}"
            }
            _isGenerating.value = false
        }
    }

    fun playTrack(track: MusicTrackEntity) {
        audioPlayer.playTrack(track)
    }

    fun toggleFavorite(track: MusicTrackEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(track)
        }
    }

    fun deleteTrack(track: MusicTrackEntity) {
        viewModelScope.launch {
            if (audioPlayer.currentTrack.value?.id == track.id) {
                audioPlayer.togglePlayPause()
            }
            repository.deleteTrack(track)
        }
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
