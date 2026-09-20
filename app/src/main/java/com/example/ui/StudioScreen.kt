package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.api.LyriaApiService
import com.example.data.MusicTrackEntity
import com.example.ui.components.AdvancedSectionHeader
import com.example.ui.components.StemsAndAcousticsControls
import com.example.ui.components.TempoAndKeyControls
import com.example.ui.components.VocalsAndLyricsControls
import com.example.ui.theme.MonoBlack
import com.example.ui.theme.MonoBorder
import com.example.ui.theme.MonoCard
import com.example.ui.theme.MonoCardElevated
import com.example.ui.theme.MonoCyan
import com.example.ui.theme.MonoCyanLight
import com.example.ui.theme.MonoGold
import com.example.ui.theme.MonoGoldGlow
import com.example.ui.theme.MonoGoldLight
import com.example.ui.theme.MonoGreenVU
import com.example.ui.theme.MonoSurfaceDark
import com.example.ui.theme.MonoTextMuted
import com.example.ui.theme.MonoTextPrimary
import com.example.ui.theme.MonoTextSecondary
import com.example.ui.theme.MonoTextTertiary
import kotlinx.coroutines.launch

@Composable
fun StudioScreen(
    onGenerate: (
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
    ) -> Unit,
    isGenerating: Boolean,
    lastGeneratedTrack: MusicTrackEntity?,
    onPlayTrack: (MusicTrackEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // Model selection: Lyria 3 Clip vs Lyria 3 Pro
    var selectedModel by remember { mutableStateOf("lyria-3-clip-preview") }

    // Prompt and genre
    var promptText by remember { mutableStateOf("Warm analog lofi beats with mellow electric piano, tape hiss, and subtle vinyl crackle") }
    var selectedGenre by remember { mutableStateOf("Lo-Fi Chillhop") }

    // Advanced options
    var bpm by remember { mutableIntStateOf(85) }
    var rootKey by remember { mutableStateOf("A") }
    var scaleMode by remember { mutableStateOf("Natural Minor") }
    var timeSignature by remember { mutableStateOf("4/4") }
    var swingPercent by remember { mutableIntStateOf(25) }

    // Vocals & Lyrics
    var includeVocals by remember { mutableStateOf(false) }
    var vocalStyle by remember { mutableStateOf("Female Soul") }
    var vocalLanguage by remember { mutableStateOf("English") }
    var customLyrics by remember { mutableStateOf("") }
    var isGeneratingLyrics by remember { mutableStateOf(false) }

    // Stems & Character
    var bassType by remember { mutableStateOf("808 Deep Sub") }
    var drumKit by remember { mutableStateOf("Vinyl Lofi Kit") }
    var leadInstrument by remember { mutableStateOf("Analog PolySynth") }
    var reverbSpace by remember { mutableStateOf("Warm Wooden Room") }

    // Section expansions
    var isTempoExpanded by remember { mutableStateOf(true) }
    var isVocalsExpanded by remember { mutableStateOf(false) }
    var isStemsExpanded by remember { mutableStateOf(false) }

    // AI Prompt Enhance state
    var isEnhancingPrompt by remember { mutableStateOf(false) }

    val presetIdeas = listOf(
        "Lo-Fi Chillhop" to "Warm vinyl lofi with soft rhodes piano and rain ambiance",
        "Cyber Synthwave" to "Pulsing 124 BPM synthwave with retro analog arpeggios and neon bass",
        "Orchestral Film" to "Cinematic dramatic strings with deep timpani and majestic French horns",
        "Deep House" to "Hypnotic 126 BPM deep house groove with sub bass and warm filtered chords",
        "Neo Soul R&B" to "Silky smooth neo-soul with jazz chord voicings and subtle vocal chops",
        "Dark Techno" to "Industrial 132 BPM underground techno with raw analog kick and acid synth"
    )

    // Pulsing animation for generation button
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp)
    ) {
        // Studio Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.hero_mono_studio),
                contentDescription = "Mono Studio Workstation",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient overlay for contrast
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                MonoSurfaceDark.copy(alpha = 0.7f),
                                MonoSurfaceDark
                            )
                        )
                    )
            )

            // Badges over hero
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(MonoBlack.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                        .border(BorderStroke(1.dp, MonoGreenVU), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MonoGreenVU)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "LYRIA 3 ACTIVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoGreenVU,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(MonoBlack.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                        .border(BorderStroke(1.dp, MonoBorder), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "48kHz STEREO",
                        fontSize = 10.sp,
                        color = MonoTextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Headline
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "MONO MUSIC STUDIO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MonoTextPrimary,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Acoustic conditioning & neural music synthesis",
                    fontSize = 12.sp,
                    color = MonoTextSecondary
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Model Selector: Clip (30s) vs Pro (Studio Track)
            Text(
                text = "LYRIA ENGINE SELECTION",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MonoTextSecondary,
                modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Lyria 3 Clip Option
                val isClipSelected = selectedModel == "lyria-3-clip-preview"
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedModel = "lyria-3-clip-preview" }
                        .testTag("model_clip_button"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isClipSelected) MonoCardElevated else MonoCard
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isClipSelected) MonoGold else MonoBorder
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Speed,
                                contentDescription = null,
                                tint = if (isClipSelected) MonoGold else MonoTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lyria 3 Clip",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isClipSelected) MonoGoldLight else MonoTextPrimary
                            )
                        }
                        Text(
                            text = "30s Fast Sketch & Loops",
                            fontSize = 11.sp,
                            color = MonoTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                // Lyria 3 Pro Option
                val isProSelected = selectedModel == "lyria-3-pro-preview"
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedModel = "lyria-3-pro-preview" }
                        .testTag("model_pro_button"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isProSelected) MonoCardElevated else MonoCard
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isProSelected) MonoCyan else MonoBorder
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = if (isProSelected) MonoCyan else MonoTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Lyria 3 Pro",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isProSelected) MonoCyanLight else MonoTextPrimary
                            )
                        }
                        Text(
                            text = "Full Multi-Section Track",
                            fontSize = 11.sp,
                            color = MonoTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prompt Input Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MonoCard),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MonoBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "COMPOSITION PROMPT",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MonoTextSecondary
                        )

                        // Gemini 3.8 Flash Prompt Enhancer
                        Button(
                            onClick = {
                                scope.launch {
                                    isEnhancingPrompt = true
                                    val enhanced = LyriaApiService.generateLyricsOrPrompt(
                                        prompt = promptText,
                                        genre = selectedGenre,
                                        theme = "Studio production",
                                        vocalStyle = vocalStyle,
                                        type = "PROMPT_ENHANCE"
                                    )
                                    promptText = enhanced
                                    isEnhancingPrompt = false
                                }
                            },
                            enabled = !isEnhancingPrompt,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("ai_enhance_prompt_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MonoCardElevated,
                                contentColor = MonoCyanLight
                            )
                        ) {
                            if (isEnhancingPrompt) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(12.dp),
                                    color = MonoCyanLight,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Gemini 3.8 Enhance", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { promptText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(96.dp)
                            .testTag("prompt_input_field"),
                        placeholder = {
                            Text(
                                "Describe your musical vision, textures, atmosphere...",
                                fontSize = 13.sp,
                                color = MonoTextMuted
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MonoGold,
                            unfocusedBorderColor = MonoBorder,
                            focusedTextColor = MonoTextPrimary,
                            unfocusedTextColor = MonoTextPrimary,
                            focusedContainerColor = MonoCardElevated,
                            unfocusedContainerColor = MonoCardElevated
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Genre/Style Inspo Chips
                    Text(
                        text = "STUDIO PRESETS",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MonoTextTertiary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        presetIdeas.forEach { (genreName, promptSnippet) ->
                            val isCurrent = selectedGenre == genreName
                            FilterChip(
                                selected = isCurrent,
                                onClick = {
                                    selectedGenre = genreName
                                    promptText = promptSnippet
                                    // Set ideal default BPM
                                    when (genreName) {
                                        "Lo-Fi Chillhop" -> bpm = 85
                                        "Cyber Synthwave" -> bpm = 124
                                        "Orchestral Film" -> bpm = 75
                                        "Deep House" -> bpm = 126
                                        "Neo Soul R&B" -> bpm = 90
                                        "Dark Techno" -> bpm = 132
                                    }
                                },
                                label = { Text(genreName, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MonoGold,
                                    selectedLabelColor = Color.Black,
                                    containerColor = MonoCardElevated,
                                    labelColor = MonoTextSecondary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ADVANCED OPTIONS SECTION
            Text(
                text = "ADVANCED COMPOSITION CONTROLS",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MonoTextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // 1. Tempo, Key & Harmony
            AdvancedSectionHeader(
                title = "TEMPO, KEY & HARMONY",
                icon = Icons.Default.Speed,
                isExpanded = isTempoExpanded,
                onToggle = { isTempoExpanded = !isTempoExpanded },
                badgeText = "$bpm BPM • $rootKey $scaleMode"
            )
            AnimatedVisibility(visible = isTempoExpanded) {
                TempoAndKeyControls(
                    bpm = bpm,
                    onBpmChange = { bpm = it },
                    rootKey = rootKey,
                    onRootKeyChange = { rootKey = it },
                    scale = scaleMode,
                    onScaleChange = { scaleMode = it },
                    timeSignature = timeSignature,
                    onTimeSignatureChange = { timeSignature = it },
                    swingPercent = swingPercent,
                    onSwingChange = { swingPercent = it }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Vocals & Lyrics
            AdvancedSectionHeader(
                title = "VOCALS & LYRIC ENGINE",
                icon = Icons.Default.Mic,
                isExpanded = isVocalsExpanded,
                onToggle = { isVocalsExpanded = !isVocalsExpanded },
                badgeText = if (includeVocals) "$vocalStyle ($vocalLanguage)" else "Instrumental"
            )
            AnimatedVisibility(visible = isVocalsExpanded) {
                VocalsAndLyricsControls(
                    includeVocals = includeVocals,
                    onIncludeVocalsChange = { includeVocals = it },
                    vocalStyle = vocalStyle,
                    onVocalStyleChange = { vocalStyle = it },
                    vocalLanguage = vocalLanguage,
                    onVocalLanguageChange = { vocalLanguage = it },
                    customLyrics = customLyrics,
                    onCustomLyricsChange = { customLyrics = it },
                    onGenerateLyricsWithGemini = {
                        scope.launch {
                            isGeneratingLyrics = true
                            val generated = LyriaApiService.generateLyricsOrPrompt(
                                prompt = promptText,
                                genre = selectedGenre,
                                theme = "Mono acoustic journey",
                                vocalStyle = vocalStyle,
                                type = "LYRICS"
                            )
                            customLyrics = generated
                            isGeneratingLyrics = false
                        }
                    },
                    isGeneratingLyrics = isGeneratingLyrics
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Stems & Acoustics
            AdvancedSectionHeader(
                title = "STEMS & ACOUSTICS MIX",
                icon = Icons.Default.GraphicEq,
                isExpanded = isStemsExpanded,
                onToggle = { isStemsExpanded = !isStemsExpanded },
                badgeText = "$bassType • $drumKit"
            )
            AnimatedVisibility(visible = isStemsExpanded) {
                StemsAndAcousticsControls(
                    bassType = bassType,
                    onBassTypeChange = { bassType = it },
                    drumKit = drumKit,
                    onDrumKitChange = { drumKit = it },
                    leadInstrument = leadInstrument,
                    onLeadInstrumentChange = { leadInstrument = it },
                    reverbSpace = reverbSpace,
                    onReverbSpaceChange = { reverbSpace = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // MAIN GENERATE BUTTON
            val stemsConfig = "$bassType, $drumKit, $leadInstrument, $reverbSpace, Swing $swingPercent%"
            Button(
                onClick = {
                    onGenerate(
                        selectedModel,
                        promptText,
                        selectedGenre,
                        bpm,
                        rootKey,
                        scaleMode,
                        timeSignature,
                        includeVocals,
                        if (includeVocals) vocalStyle else null,
                        if (includeVocals) customLyrics else null,
                        stemsConfig
                    )
                },
                enabled = !isGenerating && promptText.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .alpha(if (isGenerating) pulseAlpha else 1f)
                    .testTag("generate_music_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonoGold,
                    contentColor = Color.Black
                )
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Black,
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (selectedModel.contains("pro")) "SYNTHESIZING LYRIA 3 PRO..." else "SYNTHESIZING LYRIA 3 CLIP...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (selectedModel.contains("pro")) "GENERATE FULL TRACK (PRO)" else "GENERATE 30S CLIP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Status message / Recently created card
            if (lastGeneratedTrack != null && !isGenerating) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = MonoCardElevated),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MonoGreenVU.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPlayTrack(lastGeneratedTrack) }
                        .testTag("last_generated_card")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MonoGreenVU),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.Black
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Ready: ${lastGeneratedTrack.title}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MonoTextPrimary
                                )
                                Text(
                                    text = "Tap to listen • ${lastGeneratedTrack.bpm} BPM ${lastGeneratedTrack.rootKey}",
                                    fontSize = 11.sp,
                                    color = MonoGreenVU
                                )
                            }
                        }
                        Text(
                            text = "PLAY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoGold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
