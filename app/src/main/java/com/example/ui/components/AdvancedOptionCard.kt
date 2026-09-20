package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MonoBlack
import com.example.ui.theme.MonoBorder
import com.example.ui.theme.MonoCard
import com.example.ui.theme.MonoCardElevated
import com.example.ui.theme.MonoCyan
import com.example.ui.theme.MonoCyanLight
import com.example.ui.theme.MonoGold
import com.example.ui.theme.MonoGoldLight
import com.example.ui.theme.MonoGreenVU
import com.example.ui.theme.MonoTextMuted
import com.example.ui.theme.MonoTextPrimary
import com.example.ui.theme.MonoTextSecondary
import com.example.ui.theme.MonoTextTertiary

@Composable
fun AdvancedSectionHeader(
    title: String,
    icon: ImageVector,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    badgeText: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MonoCard)
            .border(BorderStroke(1.dp, MonoBorder), RoundedCornerShape(10.dp))
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("section_${title.lowercase().replace(" ", "_")}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MonoGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MonoTextPrimary
            )
            if (badgeText != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(MonoCardElevated, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        color = MonoCyanLight,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
        Icon(
            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = MonoTextSecondary
        )
    }
}

@Composable
fun TempoAndKeyControls(
    bpm: Int,
    onBpmChange: (Int) -> Unit,
    rootKey: String,
    onRootKeyChange: (String) -> Unit,
    scale: String,
    onScaleChange: (String) -> Unit,
    timeSignature: String,
    onTimeSignatureChange: (String) -> Unit,
    swingPercent: Int,
    onSwingChange: (Int) -> Unit
) {
    val keys = listOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
    val scales = listOf(
        "Natural Minor", "Major", "Dorian", "Phrygian",
        "Lydian", "Harmonic Minor", "Pentatonic Blues", "Japanese Insen"
    )
    val timeSignatures = listOf("4/4", "3/4", "6/8", "7/8")

    // Tap Tempo state
    val tapTimestamps = remember { mutableStateListOf<Long>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // BPM Row with Slider & Steppers
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "TEMPO (BPM)",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MonoTextSecondary
                )
                Text(
                    text = "$bpm BPM",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonoGold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Tap tempo button
            OutlinedButton(
                onClick = {
                    val now = System.currentTimeMillis()
                    tapTimestamps.add(now)
                    // Keep last 4 taps within 3 seconds
                    while (tapTimestamps.size > 4 || (tapTimestamps.isNotEmpty() && now - tapTimestamps.first() > 3000)) {
                        tapTimestamps.removeAt(0)
                    }
                    if (tapTimestamps.size >= 2) {
                        var totalDiff = 0L
                        for (i in 1 until tapTimestamps.size) {
                            totalDiff += (tapTimestamps[i] - tapTimestamps[i - 1])
                        }
                        val avgDiffMs = totalDiff / (tapTimestamps.size - 1)
                        if (avgDiffMs > 150) {
                            val calculatedBpm = (60000 / avgDiffMs).toInt().coerceIn(40, 220)
                            onBpmChange(calculatedBpm)
                        }
                    }
                },
                modifier = Modifier
                    .height(36.dp)
                    .testTag("tap_tempo_button"),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MonoCyan),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MonoCyanLight)
            ) {
                Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("TAP TEMPO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Slider & Stepper
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onBpmChange((bpm - 1).coerceAtLeast(40)) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease BPM", tint = MonoTextSecondary)
            }

            Slider(
                value = bpm.toFloat(),
                onValueChange = { onBpmChange(it.toInt()) },
                valueRange = 40f..220f,
                modifier = Modifier
                    .weight(1f)
                    .testTag("bpm_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = MonoGold,
                    activeTrackColor = MonoGold,
                    inactiveTrackColor = MonoBorder
                )
            )

            IconButton(
                onClick = { onBpmChange((bpm + 1).coerceAtMost(220)) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase BPM", tint = MonoTextSecondary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Time Signature
        Text(
            text = "TIME SIGNATURE",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MonoTextSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            timeSignatures.forEach { sig ->
                val isSelected = sig == timeSignature
                FilterChip(
                    selected = isSelected,
                    onClick = { onTimeSignatureChange(sig) },
                    label = { Text(sig, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MonoGold,
                        selectedLabelColor = Color.Black,
                        containerColor = MonoCardElevated,
                        labelColor = MonoTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) MonoGold else MonoBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Root Key Picker (horizontal scroll)
        Text(
            text = "ROOT TONAL KEY",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MonoTextSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            keys.forEach { key ->
                val isSelected = key == rootKey
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) MonoGold else MonoCardElevated)
                        .border(BorderStroke(1.dp, if (isSelected) MonoGoldLight else MonoBorder), RoundedCornerShape(8.dp))
                        .clickable { onRootKeyChange(key) }
                        .testTag("key_chip_$key"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = key,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.Black else MonoTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Musical Scale Selector
        Text(
            text = "MUSICAL SCALE / HARMONIC MODE",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MonoTextSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            scales.forEach { s ->
                val isSelected = s == scale
                FilterChip(
                    selected = isSelected,
                    onClick = { onScaleChange(s) },
                    label = { Text(s, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MonoCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = MonoCardElevated,
                        labelColor = MonoTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) MonoCyanLight else MonoBorder
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Swing / Groove
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SWING / GROOVE: $swingPercent%",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = MonoTextSecondary
            )
            Slider(
                value = swingPercent.toFloat(),
                onValueChange = { onSwingChange(it.toInt()) },
                valueRange = 0f..75f,
                steps = 2,
                modifier = Modifier.width(180.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MonoCyan,
                    activeTrackColor = MonoCyan,
                    inactiveTrackColor = MonoBorder
                )
            )
        }
    }
}

@Composable
fun VocalsAndLyricsControls(
    includeVocals: Boolean,
    onIncludeVocalsChange: (Boolean) -> Unit,
    vocalStyle: String,
    onVocalStyleChange: (String) -> Unit,
    vocalLanguage: String,
    onVocalLanguageChange: (String) -> Unit,
    customLyrics: String,
    onCustomLyricsChange: (String) -> Unit,
    onGenerateLyricsWithGemini: () -> Unit,
    isGeneratingLyrics: Boolean
) {
    val vocalStyles = listOf(
        "Female Soul", "Male Baritone", "Ethereal Whisper",
        "Auto-tuned Pop", "Vintage Jazz", "Rap & Flow", "Choir Harmonies"
    )
    val languages = listOf("English", "Japanese", "Spanish", "French", "Korean", "Italian")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Toggle Vocals
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "VOCAL TRACK",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MonoTextPrimary
                )
                Text(
                    text = if (includeVocals) "Vocalist enabled" else "Pure instrumental",
                    fontSize = 11.sp,
                    color = MonoTextSecondary
                )
            }
            Switch(
                checked = includeVocals,
                onCheckedChange = onIncludeVocalsChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MonoGold,
                    checkedTrackColor = MonoGold.copy(alpha = 0.4f),
                    uncheckedThumbColor = MonoTextMuted,
                    uncheckedTrackColor = MonoCardElevated
                ),
                modifier = Modifier.testTag("vocals_switch")
            )
        }

        AnimatedVisibility(
            visible = includeVocals,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                // Vocal Style Chips
                Text(
                    text = "VOCAL CHARACTER & TIMBRE",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MonoTextSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    vocalStyles.forEach { style ->
                        val isSelected = style == vocalStyle
                        FilterChip(
                            selected = isSelected,
                            onClick = { onVocalStyleChange(style) },
                            label = { Text(style, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MonoGold,
                                selectedLabelColor = Color.Black,
                                containerColor = MonoCardElevated,
                                labelColor = MonoTextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) MonoGold else MonoBorder
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Language
                Text(
                    text = "LYRIC LANGUAGE",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = MonoTextSecondary
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    languages.forEach { lang ->
                        val isSelected = lang == vocalLanguage
                        FilterChip(
                            selected = isSelected,
                            onClick = { onVocalLanguageChange(lang) },
                            label = { Text(lang, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MonoCyan,
                                selectedLabelColor = Color.Black,
                                containerColor = MonoCardElevated,
                                labelColor = MonoTextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Lyrics input with AI lyric writer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SONG LYRICS (OPTIONAL)",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MonoTextSecondary
                    )

                    Button(
                        onClick = onGenerateLyricsWithGemini,
                        enabled = !isGeneratingLyrics,
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("ai_lyrics_button"),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MonoCardElevated,
                            contentColor = MonoGold
                        )
                    ) {
                        if (isGeneratingLyrics) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                color = MonoGold,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gemini 3.8 Lyricist", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = customLyrics,
                    onValueChange = onCustomLyricsChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("lyrics_input"),
                    placeholder = {
                        Text(
                            "Enter custom verse/chorus lyrics or tap 'Gemini 3.8 Lyricist' to generate...",
                            fontSize = 12.sp,
                            color = MonoTextMuted
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MonoGold,
                        unfocusedBorderColor = MonoBorder,
                        focusedTextColor = MonoTextPrimary,
                        unfocusedTextColor = MonoTextSecondary,
                        focusedContainerColor = MonoCardElevated,
                        unfocusedContainerColor = MonoCardElevated
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

@Composable
fun StemsAndAcousticsControls(
    bassType: String,
    onBassTypeChange: (String) -> Unit,
    drumKit: String,
    onDrumKitChange: (String) -> Unit,
    leadInstrument: String,
    onLeadInstrumentChange: (String) -> Unit,
    reverbSpace: String,
    onReverbSpaceChange: (String) -> Unit
) {
    val bassTypes = listOf("808 Deep Sub", "Moog Analog Synth", "Slap Electric", "Acoustic Upright")
    val drumKits = listOf("808 Trap", "Vinyl Lofi Kit", "Vintage Roland TR", "Orchestral Timpani", "Minimal Acoustic")
    val leadInstruments = listOf("Analog PolySynth", "Concert Grand Piano", "Fender Strat Guitar", "Tenor Sax", "Ethereal Ambient Pads")
    val reverbSpaces = listOf("Dry Studio", "Warm Wooden Room", "Vintage Plate", "Cathedral Hall", "Cosmic Infinity")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Bassline
        Text(
            text = "BASS STEM",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MonoTextSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            bassTypes.forEach { item ->
                val isSelected = item == bassType
                FilterChip(
                    selected = isSelected,
                    onClick = { onBassTypeChange(item) },
                    label = { Text(item, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MonoGold,
                        selectedLabelColor = Color.Black,
                        containerColor = MonoCardElevated,
                        labelColor = MonoTextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Drum Kit
        Text(
            text = "PERCUSSION / DRUM STEM",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MonoTextSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            drumKits.forEach { item ->
                val isSelected = item == drumKit
                FilterChip(
                    selected = isSelected,
                    onClick = { onDrumKitChange(item) },
                    label = { Text(item, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MonoCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = MonoCardElevated,
                        labelColor = MonoTextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Lead Melodic
        Text(
            text = "LEAD INSTRUMENT STEM",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MonoTextSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            leadInstruments.forEach { item ->
                val isSelected = item == leadInstrument
                FilterChip(
                    selected = isSelected,
                    onClick = { onLeadInstrumentChange(item) },
                    label = { Text(item, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MonoGreenVU,
                        selectedLabelColor = Color.Black,
                        containerColor = MonoCardElevated,
                        labelColor = MonoTextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Reverb Space
        Text(
            text = "ACOUSTIC SPACE / REVERB",
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MonoTextSecondary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            reverbSpaces.forEach { item ->
                val isSelected = item == reverbSpace
                FilterChip(
                    selected = isSelected,
                    onClick = { onReverbSpaceChange(item) },
                    label = { Text(item, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MonoTextPrimary,
                        selectedLabelColor = Color.Black,
                        containerColor = MonoCardElevated,
                        labelColor = MonoTextSecondary
                    )
                )
            }
        }
    }
}
