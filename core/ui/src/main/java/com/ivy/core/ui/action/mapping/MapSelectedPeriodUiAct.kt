package com.ivy.core.ui.action.mapping

import android.content.Context
import androidx.annotation.StringRes
import com.ivy.common.time.dateNowLocal
import com.ivy.common.time.format
import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.ui.R
import com.ivy.core.ui.data.period.MonthUi
import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.core.ui.data.period.TimeRangeUi
import com.ivy.core.ui.data.period.fullMonthName
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.time.TimeRange
import com.ivy.data.time.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject

class MapSelectedPeriodUiAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val timeProvider: TimeProvider,
) : MapUiAction<SelectedPeriod, SelectedPeriodUi>() {
    override suspend fun transform(domain: SelectedPeriod): SelectedPeriodUi = when (domain) {
        is SelectedPeriod.AllTime -> SelectedPeriodUi.AllTime(
            periodBtnText = appContext.getString(R.string.all_time),
            rangeUi = rangeUi(domain)
        )
        is SelectedPeriod.CustomRange -> SelectedPeriodUi.CustomRange(
            periodBtnText = formatFromToPeriod(domain.range),
            rangeUi = rangeUi(domain)
        )
        is SelectedPeriod.InTheLast -> SelectedPeriodUi.InTheLast(
            periodBtnText = formatInTheLast(domain),
            n = domain.n,
            unit = domain.unit,
            rangeUi = rangeUi(domain)
        )
        is SelectedPeriod.Monthly -> SelectedPeriodUi.Monthly(
            periodBtnText = formatMonthly(domain),
            month = MonthUi(
                number = domain.month.number,
                year = domain.month.year,
                currentYear = domain.month.year == dateNowLocal().year,
                fullName = fullMonthName(appContext, monthNumber = domain.month.number),
            ),
            rangeUi = rangeUi(domain)
        )
    }

    private fun formatInTheLast(period: SelectedPeriod.InTheLast): String {
        fun unit(@StringRes one: Int, @StringRes many: Int) =
            if (period.n == 1) appContext.getString(one) else appContext.getString(many)

        // TODO: Re-work using String resource plurals
        val unit = when (period.unit) {
            TimeUnit.Day -> unit(one = R.string.day, many = R.string.days)
            TimeUnit.Week -> unit(one = R.string.week, many = R.string.weeks)
            TimeUnit.Month -> unit(one = R.string.month, many = R.string.months)
            TimeUnit.Year -> unit(one = R.string.year, many = R.string.years)
        }
        return "Last ${period.n} $unit"
    }

    private fun formatMonthly(monthly: SelectedPeriod.Monthly): String =
        if (monthly.startDayOfMonth != 1) {
            formatFromToPeriod(monthly.range)
        } else {
            val month = monthly.range.from
            val thisYear = month.year == dateNowLocal().year
            val pattern = if (thisYear) "MMMM" else "MMMM. yyyy"
            month.format(pattern)
        }

    private fun formatFromToPeriod(range: TimeRange): String {
        fun format(time: LocalDateTime, currentYear: Int): String =
            time.format(if (time.year == currentYear) "MMM dd" else "MMM dd, yyyy")

        val currentYear = timeProvider.dateNow().year
        val from = format(range.from, currentYear)
        val to = format(range.to, currentYear)
        return "$from - $to"
    }

    private fun rangeUi(selectedPeriod: SelectedPeriod): TimeRangeUi {
        fun format(time: LocalDateTime, currentYear: Int): String =
            time.format(if (time.year == currentYear) "MMM. dd" else "MMM. dd, yyyy")

        val period = selectedPeriod.range
        val currentYear = dateNowLocal().year

        return TimeRangeUi(
            range = period,
            fromText = format(period.from, currentYear),
            toText = format(period.to, currentYear)
        )
    }
}