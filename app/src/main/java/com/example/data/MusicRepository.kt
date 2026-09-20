package com.example.data

import android.content.Context
import com.example.api.GenerationResult
import com.example.api.LyriaApiService
import com.example.audio.ProceduralAudioGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File

class MusicRepository(
    private val context: Context,
    private val dao: MusicTrackDao
) {
    val allTracks: Flow<List<MusicTrackEntity>> = dao.getAllTracks()
    val favoriteTracks: Flow<List<MusicTrackEntity>> = dao.getFavoriteTracks()

    suspend fun getTrackById(id: Long): MusicTrackEntity? = dao.getTrackById(id)

    suspend fun deleteTrack(track: MusicTrackEntity) = withContext(Dispatchers.IO) {
        dao.deleteTrack(track)
        // Clean up audio file if exists
        try {
            val file = File(track.audioFilePath)
            if (file.exists()) {
                file.delete()
            }
        } catch (_: Exception) {}
    }

    suspend fun toggleFavorite(track: MusicTrackEntity) = withContext(Dispatchers.IO) {
        dao.setFavorite(track.id, !track.isFavorite)
    }

    suspend fun generateTrack(
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
    ): Result<MusicTrackEntity> = withContext(Dispatchers.IO) {
        when (val res = LyriaApiService.generateMusic(
            context = context,
            modelName = modelName,
            prompt = prompt,
            bpm = bpm,
            rootKey = rootKey,
            scale = scale,
            timeSignature = timeSignature,
            genre = genre,
            includeVocals = includeVocals,
            vocalStyle = vocalStyle,
            customLyrics = customLyrics,
            stemsConfig = stemsConfig
        )) {
            is GenerationResult.Success -> {
                val entity = MusicTrackEntity(
                    title = res.title,
                    prompt = prompt,
                    model = modelName,
                    genre = genre,
                    bpm = bpm,
                    rootKey = rootKey,
                    scale = scale,
                    timeSignature = timeSignature,
                    audioFilePath = res.audioFilePath,
                    durationSeconds = res.durationSeconds,
                    lyrics = res.generatedLyrics,
                    vocalStyle = if (includeVocals) vocalStyle else "Instrumental",
                    stemsSummary = stemsConfig,
                    createdAt = System.currentTimeMillis(),
                    isFavorite = false
                )
                val id = dao.insertTrack(entity)
                Result.success(entity.copy(id = id))
            }
            is GenerationResult.Error -> {
                Result.failure(Exception(res.message))
            }
        }
    }

    /**
     * Seeds initial studio starter tracks if the database is empty so the user has immediate audio to test.
     */
    suspend fun seedInitialTracksIfEmpty() = withContext(Dispatchers.IO) {
        val count = dao.getTrackCount()
        if (count == 0) {
            val starter1File = File(context.filesDir, "sample_midnight_drift.wav")
            ProceduralAudioGenerator.generateTrackWav(
                destFile = starter1File,
                rootKey = "A",
                scale = "Natural Minor",
                bpm = 85,
                durationSeconds = 16
            )
            val starter1 = MusicTrackEntity(
                title = "Midnight Drift",
                prompt = "Deep lofi hip-hop beat with warm rhodes electric piano and vinyl crackle",
                model = "lyria-3-clip-preview",
                genre = "Lo-Fi Beats",
                bpm = 85,
                rootKey = "A",
                scale = "Natural Minor",
                timeSignature = "4/4",
                audioFilePath = starter1File.absolutePath,
                durationSeconds = 16,
                vocalStyle = "Instrumental",
                stemsSummary = "Lo-Fi Vinyl Drums, Sub 808, Rhodes, Tape Saturation",
                isFavorite = true
            )
            dao.insertTrack(starter1)

            val starter2File = File(context.filesDir, "sample_cyber_pulse.wav")
            ProceduralAudioGenerator.generateTrackWav(
                destFile = starter2File,
                rootKey = "D",
                scale = "Dorian",
                bpm = 124,
                durationSeconds = 20
            )
            val starter2 = MusicTrackEntity(
                title = "Cyber Pulse",
                prompt = "Futuristic synthwave with pulsing analog arpeggiator and punchy retro drums",
                model = "lyria-3-pro-preview",
                genre = "Synthwave",
                bpm = 124,
                rootKey = "D",
                scale = "Dorian",
                timeSignature = "4/4",
                audioFilePath = starter2File.absolutePath,
                durationSeconds = 20,
                vocalStyle = "Instrumental",
                stemsSummary = "Moog Bass, Vintage Drum Machine, Analog Arp, Plate Reverb",
                isFavorite = false
            )
            dao.insertTrack(starter2)
        }
    }
}
