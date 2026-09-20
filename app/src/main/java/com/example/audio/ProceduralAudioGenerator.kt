package com.example.audio

import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object ProceduralAudioGenerator {

    private val NOTE_OFFSETS = mapOf(
        "C" to 0, "C#" to 1, "D" to 2, "D#" to 3, "E" to 4, "F" to 5,
        "F#" to 6, "G" to 7, "G#" to 8, "A" to 9, "A#" to 10, "B" to 11
    )

    private val SCALE_INTERVALS = mapOf(
        "Natural Minor" to intArrayOf(0, 2, 3, 5, 7, 8, 10),
        "Major" to intArrayOf(0, 2, 4, 5, 7, 9, 11),
        "Dorian" to intArrayOf(0, 2, 3, 5, 7, 9, 10),
        "Phrygian" to intArrayOf(0, 1, 3, 5, 7, 8, 10),
        "Lydian" to intArrayOf(0, 2, 4, 6, 7, 9, 11),
        "Harmonic Minor" to intArrayOf(0, 2, 3, 5, 7, 8, 11),
        "Pentatonic Blues" to intArrayOf(0, 3, 5, 6, 7, 10),
        "Japanese Insen" to intArrayOf(0, 1, 5, 7, 10)
    )

    fun getMidiFrequency(midiNote: Int): Double {
        return 440.0 * Math.pow(2.0, (midiNote - 69) / 12.0)
    }

    /**
     * Generates a 16-bit 44.1kHz stereo WAV file containing a procedural synth track
     * structured with chords, bass, arpeggio, and drum groove according to the chosen key, scale, and BPM.
     */
    fun generateTrackWav(
        destFile: File,
        rootKey: String,
        scale: String,
        bpm: Int,
        durationSeconds: Int = 16,
        hasVocals: Boolean = false
    ): File {
        val sampleRate = 44100
        val totalSamples = sampleRate * durationSeconds
        val numChannels = 2 // Stereo

        val rootOffset = NOTE_OFFSETS[rootKey] ?: 0
        val intervals = SCALE_INTERVALS[scale] ?: intArrayOf(0, 2, 3, 5, 7, 8, 10)

        // Root MIDI note around octave 3 (C3 = 48)
        val baseMidi = 48 + rootOffset

        // Scale frequencies
        val scaleFreqs = intervals.map { interval ->
            getMidiFrequency(baseMidi + interval)
        }
        val bassFreqs = intervals.map { interval ->
            getMidiFrequency(baseMidi - 12 + interval)
        }
        val arpFreqs = intervals.map { interval ->
            getMidiFrequency(baseMidi + 12 + interval)
        }

        val beatDurationSec = 60.0 / bpm.toDouble()
        val sixteenthSec = beatDurationSec / 4.0

        val leftBuffer = FloatArray(totalSamples)
        val rightBuffer = FloatArray(totalSamples)

        val totalBeats = (durationSeconds / beatDurationSec).toInt()

        for (sampleIdx in 0 until totalSamples) {
            val t = sampleIdx.toDouble() / sampleRate
            val currentBeat = t / beatDurationSec
            val chordStep = (currentBeat.toInt() / 4) % intervals.size
            val currentSixteenth = (t / sixteenthSec).toInt()

            // 1. Warm Analog Chord Pad
            val chordRoot = scaleFreqs[chordStep % scaleFreqs.size]
            val chordThird = scaleFreqs[(chordStep + 2) % scaleFreqs.size]
            val chordFifth = scaleFreqs[(chordStep + 4) % scaleFreqs.size]

            val padEnv = 0.5 + 0.5 * sin(2.0 * PI * t * 0.25)
            val padSample = (
                sin(2.0 * PI * chordRoot * t) * 0.4 +
                sin(2.0 * PI * chordThird * t) * 0.35 +
                sin(2.0 * PI * chordFifth * t) * 0.3 +
                sin(2.0 * PI * chordRoot * 2.005 * t) * 0.15 // Chorus detune
            ) * 0.25 * padEnv

            // 2. Deep Sub / Synth Bassline
            val bassFreq = bassFreqs[chordStep % bassFreqs.size]
            val bassTimeInBeat = (t % beatDurationSec) / beatDurationSec
            val bassEnv = exp(-bassTimeInBeat * 3.5)
            val bassSample = (sin(2.0 * PI * bassFreq * t) + 0.3 * sin(2.0 * PI * bassFreq * 2 * t)) * 0.35 * bassEnv

            // 3. Shimmering Arpeggiator (16th notes)
            val arpStep = currentSixteenth % arpFreqs.size
            val arpFreq = arpFreqs[arpStep]
            val arpTimeIn16th = (t % sixteenthSec) / sixteenthSec
            val arpEnv = exp(-arpTimeIn16th * 6.0)
            val arpSample = (
                sin(2.0 * PI * arpFreq * t) +
                0.2 * sin(2.0 * PI * arpFreq * 3 * t)
            ) * 0.2 * arpEnv

            // 4. Drums (Kick on 1 & 3, Snare on 2 & 4, Hi-hat on 8ths)
            val beatInBar = currentBeat % 4.0
            val beatFrac = beatInBar - beatInBar.toInt()
            var drumSample = 0.0

            // Kick (beats 0 and 2)
            if (beatInBar < 0.5 || (beatInBar >= 2.0 && beatInBar < 2.5)) {
                val kickT = beatFrac * beatDurationSec
                if (kickT < 0.15) {
                    val pitchEnv = 120.0 * exp(-kickT * 35.0) + 45.0
                    drumSample += sin(2.0 * PI * pitchEnv * kickT) * exp(-kickT * 18.0) * 0.55
                }
            }

            // Snare / Clap (beats 1 and 3)
            if ((beatInBar >= 1.0 && beatInBar < 1.4) || (beatInBar >= 3.0 && beatInBar < 3.4)) {
                val snareT = (beatInBar - beatInBar.toInt()) * beatDurationSec
                if (snareT < 0.18) {
                    val noise = (Math.random() * 2.0 - 1.0) * exp(-snareT * 22.0)
                    val body = sin(2.0 * PI * 190.0 * snareT) * exp(-snareT * 28.0)
                    drumSample += (noise * 0.4 + body * 0.35)
                }
            }

            // Hi-hat (8th notes)
            val eighthFraction = (t % (beatDurationSec / 2.0)) / (beatDurationSec / 2.0)
            val hatT = eighthFraction * (beatDurationSec / 2.0)
            if (hatT < 0.05) {
                drumSample += (Math.random() * 2.0 - 1.0) * exp(-hatT * 90.0) * 0.15
            }

            // Stereo panning
            val panL = 0.6 + 0.3 * sin(2.0 * PI * 0.2 * t)
            val panR = 0.6 - 0.3 * sin(2.0 * PI * 0.2 * t)

            leftBuffer[sampleIdx] = (padSample * panL + bassSample * 0.8 + arpSample * 0.9 + drumSample * 0.85).toFloat()
            rightBuffer[sampleIdx] = (padSample * panR + bassSample * 0.8 + arpSample * 0.5 + drumSample * 0.85).toFloat()
        }

        // Write WAV
        writeWavFile(destFile, leftBuffer, rightBuffer, sampleRate, numChannels)
        return destFile
    }

    private fun writeWavFile(
        file: File,
        left: FloatArray,
        right: FloatArray,
        sampleRate: Int,
        numChannels: Int
    ) {
        val totalAudioSamples = left.size * numChannels
        val bytesPerSample = 2 // 16-bit
        val dataChunkSize = totalAudioSamples * bytesPerSample
        val totalFileSize = 36 + dataChunkSize

        FileOutputStream(file).use { fos ->
            val header = ByteBuffer.allocate(44).apply {
                order(ByteOrder.LITTLE_ENDIAN)
                put("RIFF".toByteArray())
                putInt(totalFileSize)
                put("WAVE".toByteArray())
                put("fmt ".toByteArray())
                putInt(16) // Subchunk1Size for PCM
                putShort(1) // AudioFormat 1 = PCM
                putShort(numChannels.toShort())
                putInt(sampleRate)
                putInt(sampleRate * numChannels * bytesPerSample) // ByteRate
                putShort((numChannels * bytesPerSample).toShort()) // BlockAlign
                putShort((bytesPerSample * 8).toShort()) // BitsPerSample
                put("data".toByteArray())
                putInt(dataChunkSize)
            }
            fos.write(header.array())

            val pcmData = ByteArray(totalAudioSamples * bytesPerSample)
            val pcmBuffer = ByteBuffer.wrap(pcmData).order(ByteOrder.LITTLE_ENDIAN)

            for (i in left.indices) {
                // Clamp
                val sL = (left[i].coerceIn(-1.0f, 1.0f) * 32767).toInt().toShort()
                val sR = (right[i].coerceIn(-1.0f, 1.0f) * 32767).toInt().toShort()
                pcmBuffer.putShort(sL)
                pcmBuffer.putShort(sR)
            }
            fos.write(pcmData)
        }
    }
}
