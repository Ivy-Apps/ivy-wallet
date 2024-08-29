package com.ivy.ui.time.impl

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.base.resource.TestResourceProvider
import com.ivy.base.time.TimeConverter
import com.ivy.base.time.TimeProvider
import com.ivy.ui.R
import com.ivy.ui.time.DeviceTimePreferences
import com.ivy.ui.time.TimeFormatter.Style
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime

@RunWith(TestParameterInjector::class)
class IvyTimeFormatterTest {

    private val timeProvider = mockk<TimeProvider>()
    private val converter = mockk<TimeConverter>()
    private val deviceTimePreferences = mockk<DeviceTimePreferences>()

    private lateinit var formatter: IvyTimeFormatter

    @Before
    fun setup() {
        formatter = IvyTimeFormatter(
            resourceProvider = TestResourceProvider().apply {
                putString(R.string.yesterday, "Yesterday")
                putString(R.string.today, "Today")
                putString(R.string.tomorrow, "Tomorrow")
            },
            timeProvider = timeProvider,
            converter = converter,
            deviceTimePreferences = deviceTimePreferences
        )
    }

    enum class DateOnlyTestCases(
        val date: LocalDateTime,
        val style: Style.DateOnly,
        val today: LocalDate,
        val expectedFormatted: String
    ) {
        WITH_WEEK_DAY__TODAY(
            date = LocalDateTime.of(2021, 1, 1, 0, 0),
            style = Style.DateOnly(includeWeekDay = true),
            today = LocalDate.of(2021, 1, 1),
            expectedFormatted = "Today, Jan 1"
        ),
        WITH_WEEK_DAY__YESTERDAY(
            date = LocalDateTime.of(2021, 2, 23, 0, 0),
            style = Style.DateOnly(includeWeekDay = true),
            today = LocalDate.of(2021, 2, 24),
            expectedFormatted = "Yesterday, Feb 23"
        ),
        WITH_WEEK_DAY__TOMORROW(
            date = LocalDateTime.of(2021, 9, 8, 0, 0),
            style = Style.DateOnly(includeWeekDay = true),
            today = LocalDate.of(2021, 9, 7),
            expectedFormatted = "Tomorrow, Sept 8"
        ),
        WITH_WEEK_DAY__NON_RELATIVE(
            date = LocalDateTime.of(2024, 8, 29, 0, 0),
            style = Style.DateOnly(includeWeekDay = true),
            today = LocalDate.of(2024, 8, 7),
            expectedFormatted = "Thu, Aug 29"
        ),
        WITH_WEEK_DAY__DIFFERENT_YEAR(
            date = LocalDateTime.of(2024, 8, 29, 0, 0),
            style = Style.DateOnly(includeWeekDay = true),
            today = LocalDate.of(2023, 8, 7),
            expectedFormatted = "Thu, Aug 29 2024"
        ),
        NO_WEEK_DAY__TODAY(
            date = LocalDateTime.of(2021, 1, 1, 0, 0),
            style = Style.DateOnly(includeWeekDay = false),
            today = LocalDate.of(2021, 1, 1),
            expectedFormatted = "Jan 1"
        ),
        NO_WEEK_DAY__YESTERDAY(
            date = LocalDateTime.of(2021, 2, 23, 0, 0),
            style = Style.DateOnly(includeWeekDay = false),
            today = LocalDate.of(2021, 2, 24),
            expectedFormatted = "Feb 23"
        ),
        NO_WEEK_DAY__TOMORROW(
            date = LocalDateTime.of(2021, 9, 8, 0, 0),
            style = Style.DateOnly(includeWeekDay = false),
            today = LocalDate.of(2021, 9, 7),
            expectedFormatted = "Sept 8"
        ),
        NO_WEEK_DAY__NON_RELATIVE(
            date = LocalDateTime.of(2024, 8, 29, 0, 0),
            style = Style.DateOnly(includeWeekDay = false),
            today = LocalDate.of(2024, 8, 7),
            expectedFormatted = "Aug 29"
        ),
        NO_WEEK_DAY__DIFFERENT_YEAR(
            date = LocalDateTime.of(2024, 8, 29, 0, 0),
            style = Style.DateOnly(includeWeekDay = false),
            today = LocalDate.of(2023, 8, 7),
            expectedFormatted = "Aug 29 2024"
        ),
    }

    @Test
    fun `validate date-only formatting`(
        @TestParameter testCase: DateOnlyTestCases
    ) {
        // Given
        every { timeProvider.localDateNow() } returns testCase.today
        val date = testCase.date

        // When
        val formatted = with(formatter) { date.format(testCase.style) }

        // Then
        formatted shouldBe testCase.expectedFormatted
    }
}