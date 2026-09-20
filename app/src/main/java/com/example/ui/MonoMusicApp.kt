package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AudioPlayerBar
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
import com.example.ui.theme.MonoTextPrimary
import com.example.ui.theme.MonoTextSecondary
import com.example.ui.theme.MonoTextTertiary

@Composable
fun MonoMusicApp(
    viewModel: MonoViewModel = viewModel()
) {
    val tracks by viewModel.allTracks.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val lastGeneratedTrack by viewModel.lastGeneratedTrack.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    // Player state
    val currentTrack by viewModel.audioPlayer.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.audioPlayer.isPlaying.collectAsStateWithLifecycle()
    val currentPosMs by viewModel.audioPlayer.currentPositionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.audioPlayer.durationMs.collectAsStateWithLifecycle()
    val isLooping by viewModel.audioPlayer.isLooping.collectAsStateWithLifecycle()
    val visualizerAmplitudes by viewModel.audioPlayer.visualizerAmplitudes.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MonoBlack,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = MonoSurfaceDark,
                border = BorderStroke(0.5.dp, MonoBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Brand Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MonoBlack)
                                    .border(BorderStroke(1.dp, MonoGold), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "M",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = MonoGold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "MONO MUSIC",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MonoTextPrimary,
                                    letterSpacing = 1.5.sp
                                )
                                Text(
                                    text = "Lyria 3 Studio Engine",
                                    fontSize = 10.sp,
                                    color = MonoTextTertiary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Live audio status badge
                        Box(
                            modifier = Modifier
                                .background(MonoCardElevated, RoundedCornerShape(6.dp))
                                .border(BorderStroke(1.dp, if (isPlaying) MonoGreenVU else MonoBorder), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (isPlaying) MonoGreenVU else MonoTextTertiary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isPlaying) "OUTPUT ON" else "READY",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isPlaying) MonoGreenVU else MonoTextTertiary
                                )
                            }
                        }
                    }

                    // Studio / Vault Tabs
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = MonoSurfaceDark,
                        contentColor = MonoGold,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = MonoGold,
                                height = 2.dp
                            )
                        },
                        divider = {}
                    ) {
                        Tab(
                            selected = selectedTabIndex == 0,
                            onClick = { selectedTabIndex = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Tune,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (selectedTabIndex == 0) MonoGold else MonoTextTertiary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "STUDIO",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        letterSpacing = 1.sp,
                                        color = if (selectedTabIndex == 0) MonoGold else MonoTextSecondary
                                    )
                                }
                            },
                            modifier = Modifier.testTag("tab_studio")
                        )

                        Tab(
                            selected = selectedTabIndex == 1,
                            onClick = { selectedTabIndex = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (selectedTabIndex == 1) MonoGold else MonoTextTertiary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "VAULT (${tracks.size})",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        letterSpacing = 1.sp,
                                        color = if (selectedTabIndex == 1) MonoGold else MonoTextSecondary
                                    )
                                }
                            },
                            modifier = Modifier.testTag("tab_vault")
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Persistent Audio Player Dock
            if (currentTrack != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    AudioPlayerBar(
                        track = currentTrack,
                        isPlaying = isPlaying,
                        currentPosMs = currentPosMs,
                        durationMs = durationMs,
                        isLooping = isLooping,
                        visualizerAmplitudes = visualizerAmplitudes,
                        onTogglePlayPause = { viewModel.audioPlayer.togglePlayPause() },
                        onSeek = { viewModel.audioPlayer.seekTo(it) },
                        onToggleLoop = { viewModel.audioPlayer.toggleLoop() },
                        onToggleFavorite = { viewModel.toggleFavorite(it) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTabIndex) {
                0 -> StudioScreen(
                    onGenerate = { model, prompt, genre, bpm, key, scale, timeSig, vocals, vocalStyle, lyrics, stems ->
                        viewModel.generateMusic(
                            modelName = model,
                            prompt = prompt,
                            genre = genre,
                            bpm = bpm,
                            rootKey = key,
                            scale = scale,
                            timeSignature = timeSig,
                            includeVocals = vocals,
                            vocalStyle = vocalStyle,
                            customLyrics = lyrics,
                            stemsConfig = stems
                        )
                    },
                    isGenerating = isGenerating,
                    lastGeneratedTrack = lastGeneratedTrack,
                    onPlayTrack = { viewModel.playTrack(it) }
                )
                1 -> LibraryScreen(
                    tracks = tracks,
                    currentPlayingTrack = currentTrack,
                    isPlaying = isPlaying,
                    onPlayTrack = { viewModel.playTrack(it) },
                    onToggleFavorite = { viewModel.toggleFavorite(it) },
                    onDeleteTrack = { viewModel.deleteTrack(it) }
                )
            }
        }
    }
}
