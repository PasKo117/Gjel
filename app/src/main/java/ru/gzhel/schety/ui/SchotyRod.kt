package ru.gzhel.schety.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gzhel.schety.ui.theme.GzhelPalette
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Русские счётцы (щуп): вертикальная спица с 10 костяшками.
 * Костяшки, сдвинутые ВНИЗ к нижнему краю стана, образуют число
 * (как на настоящих счётах: отодвинул вниз — прибавил).
 *
 * @param value      цифра разряда 0..9 (число опущенных костяшек)
 * @param onValueChange  новый разряд
 * @param enabled    false — доска только показывает ответ
 */
@Composable
fun SchotyRod(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    highlight: Boolean = false,
) {
    val rodHeightPx = with(LocalDensity.current) { 150.dp.toPx() }

    Column(
        modifier = modifier
            .width(56.dp)
            .height(178.dp)
            .then(
                if (enabled) {
                    Modifier.pointerInput(Unit) {
                        detectVerticalDragGestures { change, dragAmount ->
                            change.consume()
                            // одно движение = один шаг костяшки
                            val step = (dragAmount / (rodHeightPx / 11f)).roundToInt()
                            if (step != 0) {
                                val next = (value + step).coerceIn(0, 10)
                                if (next != value) onValueChange(next)
                            } else if (abs(dragAmount) > 4f) {
                                onValueChange((value + if (dragAmount > 0) 1 else -1).coerceIn(0, 10))
                            }
                        }
                    }
                } else {
                    Modifier
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            val w = size.width
            val h = size.height
            val cx = w / 2f

            // Стан (боковые планки) и верхняя/нижняя перекладины
            val framePaint = Brush.linearGradient(
                listOf(GzhelPalette.Deep, GzhelPalette.Mid),
                Offset(0f, 0f), Offset(w, h)
            )
            drawRoundRect(
                brush = framePaint,
                topLeft = Offset(2f, 0f),
                size = Size(w - 4f, 6f),
                cornerRadius = CornerRadius(4f, 4f)
            )
            drawRoundRect(
                brush = framePaint,
                topLeft = Offset(2f, h - 6f),
                size = Size(w - 4f, 6f),
                cornerRadius = CornerRadius(4f, 4f)
            )

            // Спица (проволока)
            drawLine(
                color = GzhelPalette.Mid.copy(alpha = 0.7f),
                start = Offset(cx, 6f),
                end = Offset(cx, h - 6f),
                strokeWidth = 3f,
                cap = StrokeCap.Round,
            )

            val beadR = (w * 0.34f).coerceAtMost(h / 13f)
            val travel = h - 12f - beadR * 2f          // путь костяшки
            val topZone = 6f + beadR                    // позиция поднятой костяшки
            val bottomZone = h - 6f - beadR             // позиция опущенной костяшки

            fun beadCenter(indexFromTop: Int, down: Boolean): Float {
                val slot = (travel / 10f)
                return if (down) {
                    bottomZone - indexFromTop * slot * 0.92f
                } else {
                    topZone + (9 - indexFromTop) * slot * 0.92f
                }
            }

            for (i in 0 until 10) {
                val isDown = i < value                 // первые `value` костяшек опущены
                val cy = beadCenter(i, isDown)
                val brush = Brush.radialGradient(
                    colors = listOf(
                        if (highlight) Color(0xFF9CC4F5) else GzhelPalette.Light,
                        GzhelPalette.Mid,
                        GzhelPalette.Deep,
                    ),
                    center = Offset(cx - beadR * 0.3f, cy - beadR * 0.3f),
                    radius = beadR * 1.6f,
                )
                // тень под костяшкой
                drawCircle(
                    color = Color(0x33123C8C),
                    radius = beadR * 0.95f,
                    center = Offset(cx + 1.5f, cy + 2f),
                )
                drawCircle(brush = brush, radius = beadR, center = Offset(cx, cy))
                // блик фарфора
                drawCircle(
                    color = Color.White.copy(alpha = 0.55f),
                    radius = beadR * 0.28f,
                    center = Offset(cx - beadR * 0.32f, cy - beadR * 0.36f),
                )
            }
        }

        Text(
            text = "$value",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) GzhelPalette.Deep else GzhelPalette.Mid.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}

/** Подпись разряда под спицей. */
@Composable
fun RodLabel(text: String, isDecimalPoint: Boolean = false) {
    Text(
        text = if (isDecimalPoint) "," else text,
        fontSize = if (isDecimalPoint) 18.sp else 10.sp,
        fontWeight = if (isDecimalPoint) FontWeight.Black else FontWeight.Normal,
        color = GzhelPalette.Deep,
    )
}
