package com.ivy.base.time.impl

import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import io.kotest.common.runBlocking
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.removeEdgecases
import io.kotest.property.arbitrary.zoneOffset
import io.kotest.property.checkAll
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class StandardTimeConverterPropertyTest {

    private val timeProvider = mockk<TimeProvider>()

    private lateinit var converter: TimeConverter

    @Before
    fun setup() {
        converter = StandardTimeConverter(
            timeZoneProvider = timeProvider
        )
    }

    @Test
    fun `LocalDateTime to Instant and backwards`(): Unit = runBlocking {
        checkAll(Arb.zoneOffset(), Arb.localDateTime()) { zone, original ->
            // Given
            every { timeProvider.getZoneId() } returns zone

            // When
            val transformed = with(converter) { original.toUTC().toLocalDateTime() }

            // Then
            transformed shouldBe original
        }
    }

    @Test
    fun `Instant to LocalDateTime and backwards`(): Unit = runBlocking {
        checkAll(Arb.zoneOffset(), Arb.instant().removeEdgecases()) { zone, original ->
            // Given
            every { timeProvider.getZoneId() } returns zone

            // When
            val transformed = with(converter) { original.toLocalDateTime().toUTC() }

            // Then
            transformed shouldBe original
        }
    }

    @Test
    fun `Instant to LocalDate and backwards`(): Unit = runBlocking {
        checkAll(Arb.zoneOffset(), Arb.instant().removeEdgecases()) { zone, original ->
            // Given
            every { timeProvider.getZoneId() } returns zone

            // When
            val transformed = with(converter) {
                val originalLocalDateTime = original.toLocalDateTime()
                original.toLocalDate().atTime(originalLocalDateTime.toLocalTime()).toUTC()
            }

            // Then
            transformed shouldBe original
        }
    }

    @Test
    fun `Instant to LocalDateTime does not crash on edge-cases`(): Unit = runBlocking {
        checkAll(Arb.zoneOffset(), Arb.instant()) { zone, original ->
            // Given
            every { timeProvider.getZoneId() } returns zone

            // When
            val transformed = with(converter) {
                original.toLocalDateTime().toUTC()
            }

            // Then
            // No crashes
        }
    }

    @Test
    fun `Instant to LocalDate does not crash on edge-cases`(): Unit = runBlocking {
        checkAll(Arb.zoneOffset(), Arb.instant()) { zone, original ->
            // Given
            every { timeProvider.getZoneId() } returns zone

            // When
            val transformed = with(converter) {
                val originalLocalDateTime = original.toLocalDateTime()
                original.toLocalDate().atTime(originalLocalDateTime.toLocalTime()).toUTC()
            }

            // Then
            // No crashes
        }
    }
}