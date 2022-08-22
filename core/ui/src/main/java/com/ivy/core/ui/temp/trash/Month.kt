package com.ivy.core.ui.temp.trash

import com.ivy.base.R
import com.ivy.common.dateNowUTC
import com.ivy.core.ui.temp.stringRes
import java.time.LocalDate

data class Month(
    val monthValue: Int,
    val name: String
) {
    companion object {
        fun monthsList(): MutableList<Month> = mutableListOf(
            Month(1, stringRes(R.string.january)),
            Month(2, stringRes(R.string.february)),
            Month(3, stringRes(R.string.march)),
            Month(4, stringRes(R.string.april)),
            Month(5, stringRes(R.string.may)),
            Month(6, stringRes(R.string.june)),
            Month(7, stringRes(R.string.july)),
            Month(8, stringRes(R.string.august)),
            Month(9, stringRes(R.string.september)),
            Month(10, stringRes(R.string.october)),
            Month(11, stringRes(R.string.november)),
            Month(12, stringRes(R.string.december)),
        )

        fun fromMonthValue(code: Int): Month =
            monthsList().first { it.monthValue == code }
    }

    fun toDate(): LocalDate =
        dateNowUTC().withMonth(monthValue)


    fun incrementMonthPeriod(
        ivyContext: com.ivy.core.ui.temp.IvyWalletCtx,
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