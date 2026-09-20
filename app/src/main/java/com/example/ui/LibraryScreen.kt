package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MusicTrackEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LibraryScreen(
    tracks: List<MusicTrackEntity>,
    currentPlayingTrack: MusicTrackEntity?,
    isPlaying: Boolean,
    onPlayTrack: (MusicTrackEntity) -> Unit,
    onToggleFavorite: (MusicTrackEntity) -> Unit,
    onDeleteTrack: (MusicTrackEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showOnlyFavorites by remember { mutableStateOf(false) }
    var selectedLyricsTrack by remember { mutableStateOf<MusicTrackEntity?>(null) }
    var trackToDelete by remember { mutableStateOf<MusicTrackEntity?>(null) }

    val filteredTracks = if (showOnlyFavorites) {
        tracks.filter { it.isFavorite }
    } else {
        tracks
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("library_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "VAULT / ARCHIVE",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MonoGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${filteredTracks.size} Generated Tracks",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonoTextPrimary
                )
            }

            // Filter toggle
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = !showOnlyFavorites,
                    onClick = { showOnlyFavorites = false },
                    label = { Text("All", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MonoGold,
                        selectedLabelColor = Color.Black,
                        containerColor = MonoCard,
                        labelColor = MonoTextSecondary
                    ),
                    modifier = Modifier.testTag("filter_all_chip")
                )
                FilterChip(
                    selected = showOnlyFavorites,
                    onClick = { showOnlyFavorites = true },
                    label = { Text("Favorites", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MonoGold,
                        selectedLabelColor = Color.Black,
                        containerColor = MonoCard,
                        labelColor = MonoTextSecondary
                    ),
                    modifier = Modifier.testTag("filter_favorites_chip")
                )
            }
        }

        if (filteredTracks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MonoCardElevated),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = MonoTextTertiary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (showOnlyFavorites) "No favorite tracks yet" else "No tracks generated yet",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MonoTextPrimary
                    )
                    Text(
                        text = if (showOnlyFavorites) "Mark any track with the heart icon to save here" else "Go to Studio tab and synthesize your first track with Lyria 3!",
                        fontSize = 12.sp,
                        color = MonoTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTracks, key = { it.id }) { track ->
                    val isCurrent = currentPlayingTrack?.id == track.id
                    TrackItemCard(
                        track = track,
                        isCurrentPlaying = isCurrent,
                        isPlaying = isCurrent && isPlaying,
                        onPlayClick = { onPlayTrack(track) },
                        onFavoriteClick = { onToggleFavorite(track) },
                        onDeleteClick = { trackToDelete = track },
                        onLyricsClick = { selectedLyricsTrack = track }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(110.dp))
                }
            }
        }
    }

    // Delete Confirmation Dialog
    trackToDelete?.let { track ->
        AlertDialog(
            onDismissRequest = { trackToDelete = null },
            containerColor = MonoCardElevated,
            title = {
                Text(
                    text = "Delete Track",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonoTextPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${track.title}'? The audio file will be removed from local storage.",
                    fontSize = 13.sp,
                    color = MonoTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTrack(track)
                        trackToDelete = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { trackToDelete = null }) {
                    Text("Cancel", color = MonoTextSecondary)
                }
            }
        )
    }

    // Lyrics Dialog
    selectedLyricsTrack?.let { track ->
        AlertDialog(
            onDismissRequest = { selectedLyricsTrack = null },
            containerColor = MonoCardElevated,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lyrics, contentDescription = null, tint = MonoGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lyrics: ${track.title}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MonoTextPrimary
                    )
                }
            },
            text = {
                Text(
                    text = track.lyrics ?: "No lyrics associated with this track.",
                    fontSize = 13.sp,
                    color = MonoTextSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { selectedLyricsTrack = null }) {
                    Text("Close", color = MonoGold, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun TrackItemCard(
    track: MusicTrackEntity,
    isCurrentPlaying: Boolean,
    isPlaying: Boolean,
    onPlayClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLyricsClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()) }
    val dateString = remember(track.createdAt) { dateFormat.format(Date(track.createdAt)) }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentPlaying) MonoCardElevated else MonoCard
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (isCurrentPlaying) MonoGold else MonoBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlayClick() }
            .testTag("track_card_${track.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Play Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isCurrentPlaying) MonoGold else MonoCardElevated)
                            .border(BorderStroke(1.dp, if (isCurrentPlaying) MonoGoldLight else MonoBorder), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = if (isCurrentPlaying) Color.Black else MonoGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrentPlaying) MonoGoldLight else MonoTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${track.genre} • ${track.bpm} BPM • ${track.rootKey} ${track.scale}",
                            fontSize = 11.sp,
                            color = MonoTextSecondary,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Actions: Lyrics, Favorite, Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!track.lyrics.isNullOrBlank()) {
                        IconButton(onClick = onLyricsClick, modifier = Modifier.size(34.dp)) {
                            Icon(
                                imageVector = Icons.Default.Lyrics,
                                contentDescription = "View Lyrics",
                                tint = MonoCyanLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(onClick = onFavoriteClick, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = if (track.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (track.isFavorite) MonoGold else MonoTextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(34.dp)) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Delete",
                            tint = MonoTextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(MonoSurfaceDark, RoundedCornerShape(4.dp))
                            .border(BorderStroke(0.5.dp, MonoBorder), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (track.model.contains("pro")) "Lyria 3 Pro" else "Lyria 3 Clip",
                            fontSize = 9.sp,
                            color = MonoCyanLight,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(MonoSurfaceDark, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${track.durationSeconds}s",
                            fontSize = 9.sp,
                            color = MonoTextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (track.vocalStyle != null && track.vocalStyle != "Instrumental") {
                        Box(
                            modifier = Modifier
                                .background(MonoSurfaceDark, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = track.vocalStyle,
                                fontSize = 9.sp,
                                color = MonoGoldLight,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Text(
                    text = dateString,
                    fontSize = 10.sp,
                    color = MonoTextTertiary,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
