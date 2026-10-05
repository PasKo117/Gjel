package ru.gzhel.schety.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gzhel.schety.CalcEngine
import ru.gzhel.schety.R
import ru.gzhel.schety.ui.theme.GzhelPalette
import ru.gzhel.schety.ui.theme.drawGzhelFlower
import ru.gzhel.schety.ui.theme.drawWavyLine

/**
 * Счётная доска целиком: пять спиц (тысячи, единицы, десятые, сотые,
 * тысячные) с разделительной запятой между классами — точь-в-точь
 * как на настоящих русских счетах.
 *
 * @param milli    показание в «милли» (число × 1000)
 * @param onDigit  изменение одного разряда (индекс 0..4, цифра 0..9)
 */
@Composable
fun SchotyBoard(
    milli: Long,
    onDigit: ((index: Int, digit: Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    showNegative: Boolean = milli < 0,
) {
    val classNames = listOf(
        stringResource(R.string.class_thousands),
        stringResource(R.string.class_units),
        stringResource(R.string.class_tenths),
        stringResource(R.string.class_hundredths),
        stringResource(R.string.class_thousandths),
    )

    // отрицательный ответ: показываем модуль, знак рисуем отдельно
    val absMilli = kotlin.math.abs(milli)
    val digits = CalcEngine.digitsOf(absMilli)
    val editable = onDigit != null

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        color = GzhelPalette.White,
        border = androidx.compose.foundation.BorderStroke(2.dp, GzhelPalette.Mid),
        shadowElevation = 6.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // росписная полоса сверху
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
            ) {
                drawWavyLine(y = size.height - 4f, amplitude = 5f, wavelength = 34f,
                    color = GzhelPalette.Light, strokeWidth = 3f)
                drawGzhelFlower(size.width * 0.5f, size.height * 0.5f, size.height * 0.42f)
                drawGzhelFlower(size.width * 0.16f, size.height * 0.55f, size.height * 0.30f, GzhelPalette.Deep)
                drawGzhelFlower(size.width * 0.84f, size.height * 0.55f, size.height * 0.30f, GzhelPalette.Deep)
            }

            Spacer(Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
            ) {
                if (showNegative) {
                    Text(
                        text = "−",
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD14B4B),
                        modifier = Modifier.align(Alignment.CenterVertically),
                    )
                }
                digits.forEachIndexed { index, d ->
                    if (index == 2) {
                        // разделительная запятая — как клеймо между классами на станке
                        Box(
                            modifier = Modifier
                                .width(14.dp)
                                .align(Alignment.Bottom)
                                .padding(bottom = 26.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(",", fontSize = 26.sp, fontWeight = FontWeight.Black, color = GzhelPalette.Deep)
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        SchotyRod(
                            value = d,
                            onValueChange = { nd -> onDigit?.invoke(index, nd) },
                            enabled = editable,
                            highlight = index < 2, // целая часть чуть светлее — «рабочий класс»
                        )
                        RodLabel(classNames[index])
                    }
                }
            }

            if (!editable) {
                Text(
                    text = "= " + CalcEngine.format(if (showNegative) -absMilli else absMilli),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GzhelPalette.Deep,
                    modifier = Modifier.padding(top = 6.dp),
                )
            } else {
                Text(
                    text = stringResource(R.string.rod_hint),
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic,
                    color = GzhelPalette.Mid.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}
