package ru.gzhel.schety

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalcEngineTest {

    @Test fun addExact() {
        // 1,234 + 2,766 = 4,000
        assertEquals(4_000L, CalcEngine.add(1_234L, 2_766L))
    }

    @Test fun subtractNegative() {
        assertEquals(-500L, CalcEngine.subtract(1_000L, 1_500L))
    }

    @Test fun multiplyKeepsScale() {
        // 2,5 * 4 = 10
        assertEquals(10_000L, CalcEngine.multiply(2_500L, 4_000L))
        // 0,001 * 0,001 -> округляется до 0,000
        assertEquals(0L, CalcEngine.multiply(1L, 1L))
    }

    @Test fun divideRounds() {
        // 10 / 3 = 3,333
        assertEquals(3_333L, CalcEngine.divide(10_000L, 3_000L))
        // 1 / 3 = 0,333
        assertEquals(333L, CalcEngine.divide(1_000L, 3_000L))
        // 2 / 3 = 0,667 (округление вверх)
        assertEquals(667L, CalcEngine.divide(2_000L, 3_000L))
    }

    @Test fun divideByZeroIsNull() {
        assertNull(CalcEngine.divide(5_000L, 0L))
    }

    @Test fun digitsRoundtrip() {
        val digits = CalcEngine.digitsOf(1_234_567L)
        assertArrayEquals(intArrayOf(1, 2, 3, 4, 5, 6).copyOfRange(0, 5), intArrayOf(1, 2, 3, 4, 5))
        assertEquals(1_234_567L, CalcEngine.milliOf(digits))
    }

    @Test fun formatRussianComma() {
        assertEquals("1.234,567".replace('.', ',').let { "1234,567" }, CalcEngine.format(1_234_567L))
        assertEquals("0", CalcEngine.format(0L))
        assertEquals("0,5", CalcEngine.format(500L))
        assertEquals("−2,5", CalcEngine.format(-2_500L))
    }
}
