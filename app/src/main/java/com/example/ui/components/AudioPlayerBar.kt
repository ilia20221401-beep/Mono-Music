package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MusicTrackEntity
import com.example.ui.theme.MonoBlack
import com.example.ui.theme.MonoBorder
import com.example.ui.theme.MonoCard
import com.example.ui.theme.MonoCardElevated
import com.example.ui.theme.MonoCyan
import com.example.ui.theme.MonoCyanLight
import com.example.ui.theme.MonoGold
import com.example.ui.theme.MonoGoldLight
import com.example.ui.theme.MonoGreenVU
import com.example.ui.theme.MonoSurfaceDark
import com.example.ui.theme.MonoTextMuted
import com.example.ui.theme.MonoTextPrimary
import com.example.ui.theme.MonoTextSecondary
import com.example.ui.theme.MonoTextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerBar(
    track: MusicTrackEntity?,
    isPlaying: Boolean,
    currentPosMs: Int,
    durationMs: Int,
    isLooping: Boolean,
    visualizerAmplitudes: FloatArray,
    onTogglePlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onToggleLoop: () -> Unit,
    onToggleFavorite: (MusicTrackEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    if (track == null) return

    var showDetailSheet by remember { mutableStateOf(false) }

    val progress = if (durationMs > 0) (currentPosMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f

    // Animated rotation for vinyl disc
    val infiniteTransition = rememberInfiniteTransition(label = "disc")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { showDetailSheet = true }
            .testTag("audio_player_bar"),
        color = MonoCardElevated,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        border = BorderStroke(1.dp, MonoBorder),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Slim progress bar on top edge
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp),
                color = MonoGold,
                trackColor = MonoBorder
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vinyl Disc Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MonoBlack)
                        .border(BorderStroke(1.dp, MonoBorder), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .rotate(if (isPlaying) rotation else 0f)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(MonoGold, Color(0xFF1E1E24), MonoBlack)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title & Model info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = track.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (track.model.contains("pro")) "Lyria 3 Pro" else "Lyria 3 Clip",
                            fontSize = 10.sp,
                            color = MonoCyanLight,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "•",
                            fontSize = 10.sp,
                            color = MonoTextTertiary
                        )
                        Text(
                            text = "${track.bpm} BPM ${track.rootKey} ${track.scale.take(4)}",
                            fontSize = 10.sp,
                            color = MonoTextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Mini animated spectrum bars
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(28.dp)
                ) {
                    WaveformVisualizer(
                        amplitudes = visualizerAmplitudes,
                        isPlaying = isPlaying,
                        modifier = Modifier.fillMaxWidth(),
                        barCount = 8,
                        showDbLabels = false
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Play / Pause toggle
                IconButton(
                    onClick = onTogglePlayPause,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(MonoGold)
                        .testTag("player_play_pause_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }

    // Expanded Full Player Sheet
    if (showDetailSheet) {
        ModalBottomSheet(
            onDismissRequest = { showDetailSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = MonoSurfaceDark,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MonoBorder)
                )
            }
        ) {
            FullPlayerContent(
                track = track,
                isPlaying = isPlaying,
                currentPosMs = currentPosMs,
                durationMs = durationMs,
                isLooping = isLooping,
                visualizerAmplitudes = visualizerAmplitudes,
                onTogglePlayPause = onTogglePlayPause,
                onSeek = onSeek,
                onToggleLoop = onToggleLoop,
                onToggleFavorite = { onToggleFavorite(track) },
                onClose = { showDetailSheet = false }
            )
        }
    }
}

@Composable
fun FullPlayerContent(
    track: MusicTrackEntity,
    isPlaying: Boolean,
    currentPosMs: Int,
    durationMs: Int,
    isLooping: Boolean,
    visualizerAmplitudes: FloatArray,
    onTogglePlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onToggleLoop: () -> Unit,
    onToggleFavorite: () -> Unit,
    onClose: () -> Unit
) {
    var isDraggingSlider by remember { mutableStateOf(false) }
    var sliderTempPos by remember { mutableStateOf(0f) }

    val currentDisplayMs = if (isDraggingSlider) sliderTempPos.toInt() else currentPosMs
    val formatTime: (Int) -> String = { ms ->
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        "%d:%02d".format(min, sec)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .navigationBarsPadding()
            .padding(bottom = 24.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "NOW PLAYING",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MonoTextTertiary
                )
                Text(
                    text = if (track.model.contains("pro")) "Lyria 3 Pro (Studio Track)" else "Lyria 3 Clip (30s Preview)",
                    fontSize = 12.sp,
                    color = MonoCyanLight,
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (track.isFavorite) MonoGold else MonoTextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Spectrum Waveform Visualizer
        WaveformVisualizer(
            amplitudes = visualizerAmplitudes,
            isPlaying = isPlaying,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            barCount = 28,
            showDbLabels = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Track Title & Genre
        Text(
            text = track.title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MonoTextPrimary
        )
        Text(
            text = "${track.genre} • ${track.rootKey} ${track.scale} • ${track.bpm} BPM • ${track.timeSignature}",
            fontSize = 13.sp,
            color = MonoTextSecondary,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Scrubber / Slider
        Slider(
            value = currentDisplayMs.toFloat(),
            onValueChange = {
                isDraggingSlider = true
                sliderTempPos = it
            },
            onValueChangeFinished = {
                isDraggingSlider = false
                onSeek(sliderTempPos.toInt())
            },
            valueRange = 0f..durationMs.toFloat().coerceAtLeast(1000f),
            colors = SliderDefaults.colors(
                thumbColor = MonoGold,
                activeTrackColor = MonoGold,
                inactiveTrackColor = MonoBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("full_player_scrubber")
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatTime(currentDisplayMs),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MonoTextSecondary
            )
            Text(
                text = formatTime(durationMs),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MonoTextSecondary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onToggleLoop,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                    contentDescription = "Loop",
                    tint = if (isLooping) MonoCyan else MonoTextSecondary,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Big Play/Pause
            IconButton(
                onClick = onTogglePlayPause,
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(MonoGold)
                    .testTag("full_player_play_pause")
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Close sheet
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MonoTextSecondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Track Prompt & Details Card
        Card(
            colors = CardDefaults.cardColors(containerColor = MonoCard),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MonoBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "COMPOSITION PROMPT",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MonoTextTertiary
                )
                Text(
                    text = track.prompt,
                    fontSize = 13.sp,
                    color = MonoTextPrimary,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                )

                if (!track.stemsSummary.isNullOrBlank()) {
                    Text(
                        text = "STEMS & ACOUSTICS",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MonoTextTertiary
                    )
                    Text(
                        text = track.stemsSummary,
                        fontSize = 12.sp,
                        color = MonoCyanLight,
                        modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                    )
                }

                if (!track.lyrics.isNullOrBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lyrics,
                            contentDescription = null,
                            tint = MonoGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LYRICS",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MonoGold
                        )
                    }
                    Text(
                        text = track.lyrics,
                        fontSize = 12.sp,
                        color = MonoTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
