package com.ivy.wallet

import com.ivy.wallet.utils.hasSignificantDecimalPart
import com.ivy.wallet.utils.shortenAmount
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ShortAmountTest {
    @Test
    fun shorten_million() {
        val amount = 1586001.23

        val result = shortenAmount(amount)
        println("result = $result")

        assertEquals("1.59m", result)
    }

    @Test
    fun shorten_million2() {
        val amount = 1084001.23

        val result = shortenAmount(amount)
        println("result = $result")

        assertEquals("1.08m", result)
    }

    @Test
    fun shorten_thousands() {
        val amount = 328600.23

        val result = shortenAmount(amount)
        println("result = $result")

        assertEquals("328.60k", result)
    }

    @Test
    fun shorten_thousands2() {
        val amount = 503000.23

        val result = shortenAmount(amount)
        println("result = $result")

        assertEquals("503k", result)
    }

    @Test
    fun hasDecimalPart_true() {
        val number = 10002341.01
        assertEquals(true, hasSignificantDecimalPart(number))
    }

    @Test
    fun hasDecimalPart_false() {
        val number = 10002341.00
        assertEquals(false, hasSignificantDecimalPart(number))
    }
}