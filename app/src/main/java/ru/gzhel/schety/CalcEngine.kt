package ru.gzhel.schety

import kotlin.math.abs

/**
 * Арифметика калькулятора выполняется над ЦЕЛЫМИ числами:
 * значение хранится как "милли" (число * 1000). Это точно совпадает
 * с разрядным составом русских счетов (3 знака после запятой) и
 * избавляет от ошибок двоичных дробей (0.1 + 0.2 != 0.3).
 */
object CalcEngine {

    const val SCALE = 1000L

    /** Максимальное показание счётов в милли: 9999.999 */
    const val MAX_MILLI = 9_999_999L

    fun add(a: Long, b: Long): Long = a + b

    fun subtract(a: Long, b: Long): Long = a - b

    fun multiply(a: Long, b: Long): Long = a * b / SCALE

    /** @return null при делении на ноль; результат округляется до тысячных */
    fun divide(a: Long, b: Long): Long? {
        if (b == 0L) return null
        return roundDiv(a * SCALE, b)
    }

    private fun roundDiv(numerator: Long, denominator: Long): Long {
        val sign = if ((numerator < 0) != (denominator < 0)) -1 else 1
        val n = abs(numerator)
        val d = abs(denominator)
        // округление к ближайшему (half-up)
        return sign * ((n * 2 + d) / (2 * d))
    }

    /**
     * Разкладывает milli на 5 разрядов счётов:
     * [тысячи, единицы, десятые, сотые, тысячные], каждый разряд 0..9.
     * Отрицательный ответ отображается знаком «−» перед счётной доской.
     */
    fun digitsOf(milli: Long): IntArray {
        val clamped = milli.coerceIn(-MAX_MILLI, MAX_MILLI)
        var rest = abs(clamped)
        val digits = IntArray(5)
        for (i in 4 downTo 0) {
            digits[i] = (rest % 10L).toInt()
            rest /= 10L
        }
        return digits
    }

    /** Собирает milli из пяти разрядов (старший — тысячи). */
    fun milliOf(digits: IntArray): Long {
        var value = 0L
        for (i in 0 until 5) {
            value = value * 10 + digits.getOrElse(i) { 0 }.coerceIn(0, 9)
        }
        return value
    }

    /** Человекочитаемое число с запятой по-русски: 12,050 -> "12,05" */
    fun format(milli: Long): String {
        val negative = milli < 0
        val abs = abs(milli)
        val whole = abs / SCALE
        val frac = (abs % SCALE).toInt()
        val s = if (frac == 0) {
            whole.toString()
        } else {
            "$whole.${frac.toString().padStart(3, '0').trimEnd('0')}"
        }
        return (if (negative) "−" else "") + s.replace('.', ',')
    }

    /**
     * Проверка результата на «переполнение станка»: сумма не должна
     * превышать наибольшее показание счётов.
     */
    fun clampResult(milli: Long): Long = milli.coerceIn(-MAX_MILLI, MAX_MILLI)
}

/** Четыре доступные операции. */
enum class Operation { ADD, SUB, MUL, DIV }
