package ru.gzhel.schety.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/** Кобальтовая гамма гжели. */
object GzhelPalette {
    val Deep = Color(0xFF123C8C)     // насыщенный кобальт
    val Mid = Color(0xFF1E5AA8)      // средний синий
    val Light = Color(0xFF6FA3DC)    // бледный «подводок»
    val Sky = Color(0xFFA9C8EE)      // самый светлый тон
    val Porcelain = Color(0xFFF7FAFF) // белёсый фарфор
    val White = Color(0xFFFFFFFF)
}

/**
 * Форма кнопки в виде гжельской «капельки» (слезинки/листа),
 * которую мастера рисуют по краю посуды.
 */
class TeardropShape : Shape {
    override fun createOutline(size: Size, scale: Float, offset: Offset): Outline {
        val w = size.width
        val h = size.height
        val path = Path().apply {
            moveTo(w / 2f, h * 0.04f)
            cubicTo(w * 0.95f, h * 0.45f, w * 0.86f, h * 0.96f, w * 0.5f, h * 0.96f)
            cubicTo(w * 0.14f, h * 0.96f, w * 0.05f, h * 0.45f, w / 2f, h * 0.04f)
            close()
        }
        return Outline.Generic(path)
    }
}

/** Фирменный гжельский мазок-«капля»: заострён сверху, округл снизу. */
private fun teardropPath(cx: Float, cy: Float, r: Float): Path = Path().apply {
    moveTo(cx, cy - r * 1.5f)
    cubicTo(cx + r, cy - r * 0.3f, cx + r * 0.9f, cy + r, cx, cy + r)
    cubicTo(cx - r * 0.9f, cy + r, cx - r, cy - r * 0.3f, cx, cy - r * 1.5f)
    close()
}

/** Листочек с прожилкой — частая деталь гжельского букета. */
private fun leafPath(cx: Float, cy: Float, len: Float, angleDeg: Double): Path {
    val a = Math.toRadians(angleDeg)
    val dx = cos(a).toFloat()
    val dy = sin(a).toFloat()
    val px = -dy
    val py = dx
    val tipX = cx + dx * len
    val tipY = cy + dy * len
    val midX = cx + dx * len * 0.5f
    val midY = cy + dy * len * 0.5f
    val w = len * 0.32f
    return Path().apply {
        moveTo(cx, cy)
        quadraticTo(midX + px * w, midY + py * w, tipX, tipY)
        quadraticTo(midX - px * w, midY - py * w, cx, cy)
        close()
    }
}

/** Пятилепестковый гжельский цветок с синей сердцевиной. */
fun DrawScope.drawGzhelFlower(cx: Float, cy: Float, radius: Float, color: Color = GzhelPalette.Mid) {
    for (i in 0 until 5) {
        val angle = -90.0 + i * 72.0
        val petalR = radius * 0.52f
        val dist = radius * 0.55f
        val a = Math.toRadians(angle)
        val px = cx + cos(a).toFloat() * dist
        val py = cy + sin(a).toFloat() * dist
        drawCircle(color.copy(alpha = 0.85f), petalR, Offset(px, py))
        drawCircle(GzhelPalette.Sky, petalR * 0.45f, Offset(px, py))
    }
    drawCircle(GzhelPalette.Deep, radius * 0.22f, Offset(cx, cy))
}

/** Гжельная веточка: изогнутый стебель, листья и капли-бутоны. */
fun DrawScope.drawGzhelBranch(x: Float, y: Float, scale: Float, flip: Boolean = false) {
    val s = if (flip) -scale else scale
    val stem = Path().apply {
        moveTo(x, y)
        cubicTo(x + 30 * s, y - 10 * s, x + 55 * s, y - 45 * s, x + 60 * s, y - 90 * s)
    }
    drawPath(stem, GzhelPalette.Mid, style = Stroke(width = 3f * scale / 10f, cap = StrokeCap.Round))

    // листья вдоль стебля
    drawPath(leafPath(x + 22 * s, y - 12 * s, 20 * kotlin.math.abs(scale), if (flip) 200.0 else -35.0),
        GzhelPalette.Light)
    drawPath(leafPath(x + 45 * s, y - 42 * s, 18 * kotlin.math.abs(scale), if (flip) 160.0 else -70.0),
        GzhelPalette.Mid.copy(alpha = 0.8f))

    // капля-бутон на конце
    drawPath(teardropPath(x + 60 * s, y - 98 * s, 9 * kotlin.math.abs(scale)), GzhelPalette.Deep)

    // мелкие точки-ягодки
    val dot = 2.4f * kotlin.math.abs(scale)
    drawCircle(GzhelPalette.Mid, dot, Offset(x + 12 * s, y - 24 * s))
    drawCircle(GzhelPalette.Light, dot, Offset(x + 34 * s, y - 8 * s))
    drawCircle(GzhelPalette.Deep, dot, Offset(x + 52 * s, y - 60 * s))
}

/** Волнистая гжельная линия-оперёд (декор по краю панели). */
fun DrawScope.drawWavyLine(y: Float, amplitude: Float, wavelength: Float, color: Color, strokeWidth: Float) {
    val path = Path()
    var x = 0f
    path.moveTo(0f, y)
    var up = true
    while (x < size.width) {
        val nextX = x + wavelength / 2
        path.quadraticTo(x + wavelength / 4, y + (if (up) -amplitude else amplitude), nextX, y)
        x = nextX
        up = !up
    }
    drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
}

/**
 * Фон карточки/экрана: белый фарфор + мягкий голубой вензель.
 * Рисуется как кисть мастера: полупрозрачные мазки по углам.
 */
fun DrawScope.paintGzhelBackground() {
    drawRect(GzhelPalette.Porcelain)

    // лёгкий радиальный «отсвет глазури»
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(GzhelPalette.White, GzhelPalette.Sky.copy(alpha = 0.10f)),
            center = Offset(size.width * 0.5f, size.height * 0.35f),
            radius = hypot(size.width, size.height) * 0.7f
        )
    )

    // роспись по углам
    drawGzhelBranch(8f, size.height * 0.28f, size.width / 360f)
    drawGzhelBranch(size.width - 8f, size.height * 0.28f, size.width / 360f, flip = true)
    drawGzhelFlower(size.width * 0.12f, size.height * 0.06f, size.width * 0.045f)
    drawGzhelFlower(size.width * 0.88f, size.height * 0.06f, size.width * 0.038f, GzhelPalette.Deep)
}

/** Мазок кисти для теней костяшек. */
val beadShadowBrush: Brush = Brush.linearGradient(
    colors = listOf(Color(0xFF0B2A63), Color(0xFF1E5AA8), Color(0xFF6FA3DC)),
    start = Offset.Zero,
    end = Offset.Infinite
)
