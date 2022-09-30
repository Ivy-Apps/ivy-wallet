package com.ivy.core.ui.data.period

import android.content.Context
import androidx.compose.runtime.Immutable
import com.ivy.resources.R

@Immutable
data class MonthUi(
    val number: Int,
    val year: Int,
    val currentYear: Boolean,
    val fullName: String,
)

fun monthsList(
    context: Context, year: Int, currentYear: Boolean
): List<MonthUi> =
    (1..12).map { number ->
        MonthUi(
            number = number,
            year = year,
            currentYear = currentYear,
            fullName = fullMonthName(context = context, monthNumber = number)
        )
    }

fun fullMonthName(context: Context, monthNumber: Int): String = when (monthNumber) {
    1 -> context.getString(R.string.january)
    2 -> context.getString(R.string.february)
    3 -> context.getString(R.string.march)
    4 -> context.getString(R.string.april)
    5 -> context.getString(R.string.may)
    6 -> context.getString(R.string.june)
    7 -> context.getString(R.string.july)
    8 -> context.getString(R.string.august)
    9 -> context.getString(R.string.september)
    10 -> context.getString(R.string.october)
    11 -> context.getString(R.string.november)
    12 -> context.getString(R.string.december)
    else -> error("Invalid month with num $monthNumber. Must be in [1,12].")
}

fun dummyMonthUi() = MonthUi(number = 1, year = 1, currentYear = true, fullName = "")