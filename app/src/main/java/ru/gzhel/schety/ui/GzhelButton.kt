package ru.gzhel.schety.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gzhel.schety.ui.theme.GzhelPalette
import ru.gzhel.schety.ui.theme.TeardropShape
import ru.gzhel.schety.ui.theme.drawGzhelBranch
import ru.gzhel.schety.ui.theme.drawGzhelFlower
import ru.gzhel.schety.ui.theme.drawWavyLine

/**
 * Кнопка, расписанная гжелью: форма «капельки», кобальтовый градиент,
 * белые цветочки и веточки по краю — как на фарфоровом блюде.
 */
@Composable
fun GzhelButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, label = "press")

    Box(
        modifier = modifier
            .height(92.dp)
            .padding(horizontal = 14.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .clickable(enabled = enabled, interactionSource = interaction, indication = null) { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        // тело кнопки-капли
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        listOf(GzhelPalette.Sky, GzhelPalette.Mid, GzhelPalette.Deep)
                    ),
                    shape = TeardropShape(),
                )
        )
        // роспись поверх
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawTeardropContour()
            val w = size.width
            val h = size.height
            drawWavyLine(
                y = h * 0.80f, amplitude = 4f, wavelength = 26f,
                color = Color.White.copy(alpha = 0.75f), strokeWidth = 2.5f
            )
            drawGzhelFlower(w * 0.15f, h * 0.52f, h * 0.12f, Color.White)
            drawGzhelFlower(w * 0.85f, h * 0.52f, h * 0.12f, Color.White)
            drawGzhelBranch(w * 0.30f, h * 0.74f, w / 360f)
            drawGzhelBranch(w * 0.70f, h * 0.74f, w / 360f, flip = true)
        }
        Text(
            text = text,
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.45f),
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

/** Белая обводка контура капли — «пропись» мастера по краю. */
private fun DrawScope.drawTeardropContour() {
    val w = size.width
    val h = size.height
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(w / 2f, h * 0.09f)
        cubicTo(w * 0.92f, h * 0.46f, w * 0.84f, h * 0.92f, w * 0.5f, h * 0.92f)
        cubicTo(w * 0.16f, h * 0.92f, w * 0.08f, h * 0.46f, w / 2f, h * 0.09f)
        close()
    }
    drawPath(path, Color.White.copy(alpha = 0.85f), style = Stroke(width = 2.5f))
}
