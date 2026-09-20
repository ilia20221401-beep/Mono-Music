package com.example.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import com.example.data.MusicTrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.sin

class MonoAudioPlayer(private val context: Context) {

    private val TAG = "MonoAudioPlayer"
    private var mediaPlayer: MediaPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val _currentTrack = MutableStateFlow<MusicTrackEntity?>(null)
    val currentTrack: StateFlow<MusicTrackEntity?> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPositionMs = MutableStateFlow(0)
    val currentPositionMs: StateFlow<Int> = _currentPositionMs.asStateFlow()

    private val _durationMs = MutableStateFlow(1)
    val durationMs: StateFlow<Int> = _durationMs.asStateFlow()

    private val _isLooping = MutableStateFlow(false)
    val isLooping: StateFlow<Boolean> = _isLooping.asStateFlow()

    // 16 spectral frequency bars for live reactive waveform / EQ visualizer
    private val _visualizerAmplitudes = MutableStateFlow(FloatArray(16) { 0.1f })
    val visualizerAmplitudes: StateFlow<FloatArray> = _visualizerAmplitudes.asStateFlow()

    fun playTrack(track: MusicTrackEntity) {
        val file = File(track.audioFilePath)
        if (!file.exists()) {
            Log.e(TAG, "Audio file not found: ${track.audioFilePath}")
            return
        }

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null

            mediaPlayer = MediaPlayer().apply {
                setDataSource(track.audioFilePath)
                isLooping = _isLooping.value
                prepare()
                start()
                setOnCompletionListener {
                    if (!_isLooping.value) {
                        _isPlaying.value = false
                        _currentPositionMs.value = 0
                        stopProgressTicker()
                    }
                }
            }

            _currentTrack.value = track
            _isPlaying.value = true
            _durationMs.value = (mediaPlayer?.duration ?: (track.durationSeconds * 1000)).coerceAtLeast(1000)
            startProgressTicker()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start media player", e)
            _isPlaying.value = false
        }
    }

    fun togglePlayPause() {
        val player = mediaPlayer ?: run {
            _currentTrack.value?.let { playTrack(it) }
            return
        }

        if (player.isPlaying) {
            player.pause()
            _isPlaying.value = false
            stopProgressTicker()
        } else {
            player.start()
            _isPlaying.value = true
            startProgressTicker()
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let { player ->
            player.seekTo(positionMs)
            _currentPositionMs.value = positionMs
        }
    }

    fun toggleLoop() {
        val newLoop = !_isLooping.value
        _isLooping.value = newLoop
        mediaPlayer?.isLooping = newLoop
    }

    private fun startProgressTicker() {
        stopProgressTicker()
        progressJob = scope.launch {
            var step = 0
            while (isActive && _isPlaying.value) {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        _currentPositionMs.value = player.currentPosition
                        _durationMs.value = player.duration.coerceAtLeast(1000)

                        // Generate lively audio frequency spectrum simulation based on playback time
                        val t = player.currentPosition / 100.0
                        val bars = FloatArray(16) { i ->
                            val freqVar = sin(t * (0.8 + i * 0.15) + i * 0.4) * 0.45 + 0.5
                            val bassBoost = if (i < 4) 0.25f else 0.05f
                            (freqVar.toFloat() + bassBoost).coerceIn(0.12f, 0.98f)
                        }
                        _visualizerAmplitudes.value = bars
                    }
                }
                delay(80)
                step++
            }
            // Reset visualizer to idle floor when not playing
            _visualizerAmplitudes.value = FloatArray(16) { 0.1f }
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTicker()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
