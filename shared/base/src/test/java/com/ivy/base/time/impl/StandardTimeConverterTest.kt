package com.ivy.base.time.impl

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@RunWith(TestParameterInjector::class)
class StandardTimeConverterTest {

    private val timeProvider = mockk<TimeProvider>()

    private lateinit var converter: TimeConverter

    @Before
    fun setup() {
        converter = StandardTimeConverter(
            timeZoneProvider = timeProvider
        )
    }

    // region Instant -> LocalDateTime
    enum class InstantToLocalDateTimeTestCase(
        val instant: Instant,
        val zoneOffset: ZoneOffset,
        val expectedLocal: LocalDateTime,
    ) {
        // Standard cases
        UTC(
            instant = Instant.parse("2024-08-04T12:00:00Z"),
            zoneOffset = ZoneOffset.UTC,
            expectedLocal = LocalDateTime.of(2024, 8, 4, 12, 0)
        ),
        UTC_PLUS_ONE(
            instant = Instant.parse("2024-08-04T12:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(1),
            expectedLocal = LocalDateTime.of(2024, 8, 4, 13, 0)
        ),
        UTC_MINUS_SEVEN(
            instant = Instant.parse("2024-08-04T12:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(-7),
            expectedLocal = LocalDateTime.of(2024, 8, 4, 5, 0)
        ),
        UTC_PLUS_FIVE_THIRTY(
            instant = Instant.parse("2024-08-04T12:00:00Z"),
            zoneOffset = ZoneOffset.ofHoursMinutes(5, 30),
            expectedLocal = LocalDateTime.of(2024, 8, 4, 17, 30)
        ),
        UTC_MINUS_THREE(
            instant = Instant.parse("2024-08-04T12:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(-3),
            expectedLocal = LocalDateTime.of(2024, 8, 4, 9, 0)
        ),

        // Date change cases
        UTC_PLUS_14(
            instant = Instant.parse("2024-08-04T23:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(14),
            expectedLocal = LocalDateTime.of(2024, 8, 5, 13, 0)
        ),
        UTC_MINUS_12(
            instant = Instant.parse("2024-08-04T00:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(-12),
            expectedLocal = LocalDateTime.of(2024, 8, 3, 12, 0)
        ),

        // Edge cases
        UTC_23_59(
            instant = Instant.parse("2024-08-04T23:59:00Z"),
            zoneOffset = ZoneOffset.UTC,
            expectedLocal = LocalDateTime.of(2024, 8, 4, 23, 59)
        ),
        UTC_PLUS_ONE_23_59(
            instant = Instant.parse("2024-08-04T22:59:00Z"),
            zoneOffset = ZoneOffset.ofHours(1),
            expectedLocal = LocalDateTime.of(2024, 8, 4, 23, 59)
        ),
        UTC_MINUS_ONE_00_00(
            instant = Instant.parse("2024-08-04T01:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(-1),
            expectedLocal = LocalDateTime.of(2024, 8, 4, 0, 0)
        ),
        UTC_00_00(
            instant = Instant.parse("2024-08-04T00:00:00Z"),
            zoneOffset = ZoneOffset.UTC,
            expectedLocal = LocalDateTime.of(2024, 8, 4, 0, 0)
        ),
        UTC_24_00(
            instant = Instant.parse("2024-08-03T00:00:00Z"),
            zoneOffset = ZoneOffset.UTC,
            expectedLocal = LocalDateTime.of(2024, 8, 3, 0, 0)
        )
    }

    @Test
    fun `validate Instant (UTC) to LocalDateTime`(
        @TestParameter testCase: InstantToLocalDateTimeTestCase
    ) {
        // Given
        val instant = testCase.instant
        every { timeProvider.getZoneId() } returns testCase.zoneOffset

        // When
        val local = with(converter) { instant.toLocalDateTime() }

        // Then
        local shouldBe testCase.expectedLocal
    }
    // endregion

    // region Instant -> LocalDate
    enum class InstantToLocalDateTestCase(
        val instant: Instant,
        val zoneOffset: ZoneOffset,
        val expectedLocalDate: LocalDate,
    ) {
        // Standard cases
        UTC(
            instant = Instant.parse("2024-08-04T12:00:00Z"),
            zoneOffset = ZoneOffset.UTC,
            expectedLocalDate = LocalDate.of(2024, 8, 4)
        ),
        UTC_PLUS_ONE(
            instant = Instant.parse("2024-08-04T12:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(1),
            expectedLocalDate = LocalDate.of(2024, 8, 4)
        ),
        UTC_MINUS_SEVEN(
            instant = Instant.parse("2024-08-04T12:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(-7),
            expectedLocalDate = LocalDate.of(2024, 8, 4)
        ),
        UTC_PLUS_FIVE_THIRTY(
            instant = Instant.parse("2024-08-04T12:00:00Z"),
            zoneOffset = ZoneOffset.ofHoursMinutes(5, 30),
            expectedLocalDate = LocalDate.of(2024, 8, 4)
        ),
        UTC_MINUS_THREE(
            instant = Instant.parse("2024-08-04T12:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(-3),
            expectedLocalDate = LocalDate.of(2024, 8, 4)
        ),

        // Date change cases
        UTC_PLUS_14(
            instant = Instant.parse("2024-08-04T23:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(14),
            expectedLocalDate = LocalDate.of(2024, 8, 5)
        ),
        UTC_MINUS_12(
            instant = Instant.parse("2024-08-04T00:00:00Z"),
            zoneOffset = ZoneOffset.ofHours(-12),
            expectedLocalDate = LocalDate.of(2024, 8, 3)
        )
    }

    @Test
    fun `validate Instant (UTC) to LocalDate`(
        @TestParameter testCase: InstantToLocalDateTestCase
    ) {
        // Given
        val instant = testCase.instant
        every { timeProvider.getZoneId() } returns testCase.zoneOffset

        // When
        val localDate = with(converter) { instant.toLocalDate() }

        // Then
        localDate shouldBe testCase.expectedLocalDate
    }
    // endregion

    // region LocalDateTime -> Instant
    enum class LocalDateTimeToUTCTestCase(
        val localDateTime: LocalDateTime,
        val zoneOffset: ZoneOffset,
        val expectedInstant: Instant,
    ) {
        // Standard cases
        UTC(
            localDateTime = LocalDateTime.of(2024, 8, 4, 12, 0),
            zoneOffset = ZoneOffset.UTC,
            expectedInstant = Instant.parse("2024-08-04T12:00:00Z")
        ),
        UTC_PLUS_ONE(
            localDateTime = LocalDateTime.of(2024, 8, 4, 13, 0),
            zoneOffset = ZoneOffset.ofHours(1),
            expectedInstant = Instant.parse("2024-08-04T12:00:00Z")
        ),
        UTC_MINUS_SEVEN(
            localDateTime = LocalDateTime.of(2024, 8, 4, 5, 0),
            zoneOffset = ZoneOffset.ofHours(-7),
            expectedInstant = Instant.parse("2024-08-04T12:00:00Z")
        ),
        UTC_PLUS_FIVE_THIRTY(
            localDateTime = LocalDateTime.of(2024, 8, 4, 17, 30),
            zoneOffset = ZoneOffset.ofHoursMinutes(5, 30),
            expectedInstant = Instant.parse("2024-08-04T12:00:00Z")
        ),
        UTC_MINUS_THREE(
            localDateTime = LocalDateTime.of(2024, 8, 4, 9, 0),
            zoneOffset = ZoneOffset.ofHours(-3),
            expectedInstant = Instant.parse("2024-08-04T12:00:00Z")
        ),

        // Date change cases
        UTC_PLUS_14(
            localDateTime = LocalDateTime.of(2024, 8, 5, 13, 0),
            zoneOffset = ZoneOffset.ofHours(14),
            expectedInstant = Instant.parse("2024-08-04T23:00:00Z")
        ),
        UTC_MINUS_12(
            localDateTime = LocalDateTime.of(2024, 8, 3, 12, 0),
            zoneOffset = ZoneOffset.ofHours(-12),
            expectedInstant = Instant.parse("2024-08-04T00:00:00Z")
        )
    }

    @Test
    fun `validate LocalDateTime to Instant (UTC)`(
        @TestParameter testCase: LocalDateTimeToUTCTestCase
    ) {
        // Given
        val localDateTime = testCase.localDateTime
        every { timeProvider.getZoneId() } returns testCase.zoneOffset

        // When
        val instant = with(converter) { localDateTime.toUTC() }

        // Then
        instant shouldBe testCase.expectedInstant
    }
    // endregion
}