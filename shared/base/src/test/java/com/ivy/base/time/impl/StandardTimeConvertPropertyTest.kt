package com.ivy.base.time.impl

import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import io.kotest.common.runBlocking
import io.kotest.property.Arb
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.removeEdgecases
import io.kotest.property.arbitrary.zoneOffset
import io.kotest.property.forAll
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class StandardTimeConvertPropertyTest {

    private val timeProvider = mockk<TimeProvider>()

    private lateinit var converter: TimeConverter

    @Before
    fun setup() {
        converter = StandardTimeConvert(
            timeZoneProvider = timeProvider
        )
    }

    @Test
    fun `LocalDateTime-Instant isomorphism`(): Unit = runBlocking {
        forAll(Arb.zoneOffset(), Arb.localDateTime().removeEdgecases()) { zone, original ->
            // Given
            every { timeProvider.getZoneId() } returns zone

            // When
            val transformed = with(converter) { original.toUTC().toLocalDateTime() }

            // Then
            transformed == original
        }

        forAll(Arb.zoneOffset(), Arb.instant().removeEdgecases()) { zone, original ->
            // Given
            every { timeProvider.getZoneId() } returns zone

            // When
            val transformed = with(converter) { original.toLocalDateTime().toUTC() }

            // Then
            transformed == original
        }
    }

    @Test
    fun `Instant-LocalDate isomorphism up to the same day`(): Unit = runBlocking {
        forAll(Arb.zoneOffset(), Arb.instant().removeEdgecases()) { zone, original ->
            // Given
            every { timeProvider.getZoneId() } returns zone

            // When
            val transformed = with(converter) {
                val originalLocalDateTime = original.toLocalDateTime()
                original.toLocalDate().atTime(originalLocalDateTime.toLocalTime()).toUTC()
            }

            // Then
            transformed == original
        }
    }
}