package ru.gzhel.schety.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.gzhel.schety.CalcEngine
import ru.gzhel.schety.Operation
import ru.gzhel.schety.R
import ru.gzhel.schety.ui.theme.GzhelPalette
import ru.gzhel.schety.ui.theme.drawGzhelFlower

/** Экран вычислений: два числа на счётах, операция из списка, ответ на счётах. */
@Composable
fun CalculateScreen(onBack: () -> Unit) {
    var firstMilli by remember { mutableLongStateOf(0L) }
    var secondMilli by remember { mutableLongStateOf(0L) }
    var operation by remember { mutableStateOf(Operation.ADD) }
    var result by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // строку с ошибкой готовим здесь, вне лямбды — stringResource нельзя звать из onClick
    val divByZeroMsg = stringResource(R.string.btn_divide_by_zero)

    fun setDigit(current: Long, index: Int, digit: Int): Long {
        val d = CalcEngine.digitsOf(current)
        d[index] = digit
        return CalcEngine.milliOf(d)
    }

    fun compute() {
        error = null
        result = when (operation) {
            Operation.ADD -> CalcEngine.clampResult(CalcEngine.add(firstMilli, secondMilli))
            Operation.SUB -> CalcEngine.subtract(firstMilli, secondMilli)
                .coerceIn(-CalcEngine.MAX_MILLI, CalcEngine.MAX_MILLI)
            Operation.MUL -> CalcEngine.multiply(firstMilli, secondMilli)
                .coerceIn(-CalcEngine.MAX_MILLI, CalcEngine.MAX_MILLI)
            Operation.DIV -> {
                val r = CalcEngine.divide(firstMilli, secondMilli)
                if (r == null) {
                    error = divByZeroMsg
                    null
                } else r.coerceIn(-CalcEngine.MAX_MILLI, CalcEngine.MAX_MILLI)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        GzhelHeader(stringResource(R.string.title_calc))

        // ── Первое число ───────────────────────────────
        SectionLabel(stringResource(R.string.label_first), CalcEngine.format(firstMilli))
        SchotyBoard(
            milli = firstMilli,
            onDigit = { i, d -> firstMilli = setDigit(firstMilli, i, d); result = null },
        )

        Spacer(Modifier.height(14.dp))

        // ── Второе число ───────────────────────────────
        SectionLabel(stringResource(R.string.label_second), CalcEngine.format(secondMilli))
        SchotyBoard(
            milli = secondMilli,
            onDigit = { i, d -> secondMilli = setDigit(secondMilli, i, d); result = null },
        )

        Spacer(Modifier.height(14.dp))

        // ── Операция (выпадающий список) ───────────────
        SectionLabel(stringResource(R.string.label_operation), null)
        OperationDropdown(
            selected = operation,
            onSelect = { operation = it; result = null },
        )

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            GzhelButton(text = stringResource(R.string.btn_clear), onClick = {
                firstMilli = 0L; secondMilli = 0L; result = null; error = null
            })
            GzhelButton(text = stringResource(R.string.btn_compute), onClick = { compute() })
        }

        Spacer(Modifier.height(10.dp))

        if (error != null) {
            Text(
                text = "☝ " + error!!,
                color = Color(0xFFD14B4B),
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(vertical = 6.dp),
            )
        }

        // ── Ответ на отдельных счётах ──────────────────
        val res = result
        if (res != null) {
            SectionLabel(stringResource(R.string.label_answer), CalcEngine.format(res))
            SchotyBoard(
                milli = res,
                showNegative = res < 0,
            )
        }

        Spacer(Modifier.height(14.dp))
        GzhelButton(text = stringResource(R.string.btn_back), onClick = onBack)
    }
}

/** Подпись секции с текущим значением. */
@Composable
private fun SectionLabel(title: String, value: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = GzhelPalette.Deep,
        )
        if (value != null) {
            Text(
                text = value,
                fontSize = 19.sp,
                fontWeight = FontWeight.Black,
                color = GzhelPalette.Mid,
            )
        }
    }
}

/** Выпадающий список операций в гжельском стиле. */
@Composable
private fun OperationDropdown(selected: Operation, onSelect: (Operation) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val opName = stringResource(
        when (selected) {
            Operation.ADD -> R.string.op_add
            Operation.SUB -> R.string.op_sub
            Operation.MUL -> R.string.op_mul
            Operation.DIV -> R.string.op_div
        }
    )

    Box(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(GzhelPalette.White, GzhelPalette.Sky.copy(alpha = 0.35f))),
                    RoundedCornerShape(20.dp),
                )
                .border(2.dp, GzhelPalette.Mid, RoundedCornerShape(20.dp))
                .clickable { expanded = true }
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(opName, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GzhelPalette.Deep)
            Canvas(modifier = Modifier.width(26.dp).height(26.dp)) {
                drawGzhelFlower(size.width / 2f, size.height / 2f, size.minDimension * 0.45f)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .padding(6.dp),
        ) {
            val items = listOf(
                Operation.ADD to R.string.op_add,
                Operation.SUB to R.string.op_sub,
                Operation.MUL to R.string.op_mul,
                Operation.DIV to R.string.op_div,
            )
            items.forEach { (op, nameRes) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(nameRes),
                            fontSize = 17.sp,
                            fontWeight = if (op == selected) FontWeight.Black else FontWeight.Normal,
                            color = if (op == selected) GzhelPalette.Deep else GzhelPalette.Mid,
                        )
                    },
                    onClick = {
                        onSelect(op)
                        expanded = false
                    },
                )
            }
        }
    }
}
