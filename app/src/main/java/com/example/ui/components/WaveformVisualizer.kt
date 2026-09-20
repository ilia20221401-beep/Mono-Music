package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MonoBorder
import com.example.ui.theme.MonoCard
import com.example.ui.theme.MonoCyan
import com.example.ui.theme.MonoGold
import com.example.ui.theme.MonoGreenVU
import com.example.ui.theme.MonoTextTertiary

@Composable
fun WaveformVisualizer(
    amplitudes: FloatArray,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 24,
    showDbLabels: Boolean = true
) {
    Box(
        modifier = modifier
            .background(MonoCard, RoundedCornerShape(12.dp))
            .padding(10.dp)
            .testTag("waveform_visualizer")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalWidth = size.width
            val totalHeight = size.height
            val spacing = 4.dp.toPx()
            val availableWidth = totalWidth - (spacing * (barCount - 1))
            val barWidth = availableWidth / barCount

            // Gradient from Cyan at low frequencies to Gold/Amber at mid to VU Green/Red at peak
            val barBrush = Brush.verticalGradient(
                colors = listOf(
                    MonoGreenVU,
                    MonoGold,
                    MonoCyan
                ),
                startY = 0f,
                endY = totalHeight
            )

            for (i in 0 until barCount) {
                // Map bar index to amplitude array
                val ampIndex = (i * amplitudes.size) / barCount
                val rawAmp = amplitudes.getOrElse(ampIndex) { 0.1f }
                val targetAmp = if (isPlaying) rawAmp else 0.08f

                val barHeight = (targetAmp * (totalHeight - 8.dp.toPx())).coerceAtLeast(4.dp.toPx())
                val x = i * (barWidth + spacing)
                val y = totalHeight - barHeight

                drawRoundRect(
                    brush = barBrush,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                )

                // Top glow tip on high amplitude
                if (targetAmp > 0.6f && isPlaying) {
                    drawCircle(
                        color = MonoGold,
                        radius = (barWidth / 2f).coerceAtMost(3.dp.toPx()),
                        center = Offset(x + barWidth / 2f, y)
                    )
                }
            }

            // Subtle horizontal reference grid lines (-6dB, -12dB, -18dB)
            val gridColor = MonoBorder.copy(alpha = 0.4f)
            drawLine(
                color = gridColor,
                start = Offset(0f, totalHeight * 0.25f),
                end = Offset(totalWidth, totalHeight * 0.25f),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = gridColor,
                start = Offset(0f, totalHeight * 0.5f),
                end = Offset(totalWidth, totalHeight * 0.5f),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = gridColor,
                start = Offset(0f, totalHeight * 0.75f),
                end = Offset(totalWidth, totalHeight * 0.75f),
                strokeWidth = 1.dp.toPx()
            )
        }

        if (showDbLabels) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "VU 48kHz",
                    fontSize = 9.sp,
                    color = MonoTextTertiary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isPlaying) "SPECTRUM ACTIVE" else "STANDBY",
                    fontSize = 9.sp,
                    color = if (isPlaying) MonoGreenVU else MonoTextTertiary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
