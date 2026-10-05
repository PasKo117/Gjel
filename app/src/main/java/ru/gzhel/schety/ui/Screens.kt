package ru.gzhel.schety.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gzhel.schety.R
import ru.gzhel.schety.ui.theme.GzhelPalette
import ru.gzhel.schety.ui.theme.drawGzhelFlower
import ru.gzhel.schety.ui.theme.drawWavyLine

/** Фарфоровая панель с кобальтовой оплёткой — контейнер для контента. */
@Composable
fun GzhelCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(28.dp))
            .background(Color.White, RoundedCornerShape(28.dp))
            .padding(16.dp),
    ) { content() }
}

/** Заголовок экрана в гжельской оплётке. */
@Composable
fun GzhelHeader(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            fontSize = 27.sp,
            fontWeight = FontWeight.Black,
            color = GzhelPalette.Deep,
            letterSpacing = 0.5.sp,
        )
        Spacer(Modifier.height(4.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
        ) {
            drawWavyLine(
                y = size.height - 3f, amplitude = 4f, wavelength = 28f,
                color = GzhelPalette.Mid, strokeWidth = 3f
            )
            drawGzhelFlower(size.width / 2f, size.height / 2f, 6f)
        }
    }
}

/** Главный экран приложения. */
@Composable
fun HomeScreen(onCalculate: () -> Unit, onAbout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // «тарелка» с названием
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(GzhelPalette.Deep, GzhelPalette.Mid)
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawWavyLine(y = size.height * 0.18f, amplitude = 6f, wavelength = 40f,
                    color = Color.White.copy(alpha = 0.5f), strokeWidth = 3f)
                drawWavyLine(y = size.height * 0.86f, amplitude = 6f, wavelength = 40f,
                    color = Color.White.copy(alpha = 0.5f), strokeWidth = 3f)
                for (fx in listOf(0.10f, 0.28f, 0.72f, 0.90f)) {
                    drawGzhelFlower(size.width * fx, size.height * 0.30f, size.height * 0.09f, Color.White)
                    drawGzhelFlower(size.width * (1f - fx), size.height * 0.74f, size.height * 0.07f, GzhelPalette.Sky)
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                )
                Text(
                    text = stringResource(R.string.subtitle),
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = GzhelPalette.Sky,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        GzhelCard {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.hint_main),
                    fontSize = 14.sp,
                    color = GzhelPalette.Deep,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
                // мини-превью счетов
                SchotyBoard(milli = 1_234_567L)
            }
        }

        Spacer(Modifier.height(16.dp))

        GzhelButton(text = stringResource(R.string.btn_calculate), onClick = onCalculate)
        Spacer(Modifier.height(10.dp))
        GzhelButton(text = stringResource(R.string.btn_about), onClick = onAbout)
    }
}

/** Диалог «О гжели». */
@Composable
fun AboutDialog(onClose: () -> Unit) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onClose,
        confirmButton = {
            Text(
                text = stringResource(R.string.about_close),
                color = GzhelPalette.Deep,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp),
            )
        },
        title = {
            Text(stringResource(R.string.about_title), color = GzhelPalette.Deep, fontWeight = FontWeight.Black)
        },
        text = {
            Text(stringResource(R.string.about_text), color = GzhelPalette.Deep, fontSize = 14.sp)
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(26.dp),
    )
}
