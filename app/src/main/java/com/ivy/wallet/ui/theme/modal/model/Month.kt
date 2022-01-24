package com.ivy.wallet.ui.theme.modal.model

import com.ivy.wallet.base.dateNowUTC
import com.ivy.wallet.ui.IvyContext
import com.ivy.wallet.ui.onboarding.model.TimePeriod
import java.time.LocalDate

data class Month(
    val monthValue: Int,
    val name: String
) {
    companion object {
        fun monthsList(): MutableList<Month> = mutableListOf(
            Month(1, "January"),
            Month(2, "February"),
            Month(3, "March"),
            Month(4, "April"),
            Month(5, "May"),
            Month(6, "June"),
            Month(7, "July"),
            Month(8, "August"),
            Month(9, "September"),
            Month(10, "October"),
            Month(11, "November"),
            Month(12, "December"),
        )

        fun fromMonthValue(code: Int): Month =
            monthsList().first { it.monthValue == code }
    }

    fun toDate(): LocalDate =
        dateNowUTC()
            .withMonth(monthValue)


    fun incrementMonthPeriod(
        ivyContext: IvyContext,
        increment: Long,
        year: Int
    ): TimePeriod {
        val incrementedMonth = toDate().withYear(year).plusMonths(increment)
        val incrementedPeriod = TimePeriod(
            month = fromMonthValue(incrementedMonth.monthValue),
            year = incrementedMonth.year
        )
        ivyContext.updateSelectedPeriodInMemory(incrementedPeriod)
        return incrementedPeriod
    }

    fun toTimePeriod(): TimePeriod = TimePeriod(
        month = this
    )
}