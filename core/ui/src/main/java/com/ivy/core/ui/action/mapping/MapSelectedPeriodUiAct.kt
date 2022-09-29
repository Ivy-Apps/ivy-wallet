package com.ivy.core.ui.action.mapping

import android.content.Context
import androidx.annotation.StringRes
import com.ivy.common.dateNowLocal
import com.ivy.common.format
import com.ivy.core.domain.pure.time.period
import com.ivy.core.ui.R
import com.ivy.core.ui.data.period.MonthUi
import com.ivy.core.ui.data.period.PeriodUi
import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.core.ui.data.period.fullMonthName
import com.ivy.data.time.Period
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.time.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import javax.inject.Inject

class MapSelectedPeriodUiAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
) : MapUiAction<SelectedPeriod, SelectedPeriodUi>() {
    override suspend fun transform(domain: SelectedPeriod): SelectedPeriodUi = when (domain) {
        is SelectedPeriod.AllTime -> SelectedPeriodUi.AllTime(
            btnText = appContext.getString(R.string.all_time),
            periodUi = periodUi(domain)
        )
        is SelectedPeriod.CustomRange -> SelectedPeriodUi.CustomRange(
            btnText = formatFromToPeriod(domain.period),
            periodUi = periodUi(domain)
        )
        is SelectedPeriod.InTheLast -> SelectedPeriodUi.InTheLast(
            btnText = formatInTheLast(domain),
            n = domain.n,
            unit = domain.unit,
            periodUi = periodUi(domain)
        )
        is SelectedPeriod.Monthly -> SelectedPeriodUi.Monthly(
            btnText = formatMonthly(domain),
            month = MonthUi(
                number = domain.month.number,
                year = domain.month.year,
                currentYear = domain.month.year == dateNowLocal().year,
                fullName = fullMonthName(appContext, monthNumber = domain.month.number),
            ),
            periodUi = periodUi(domain)
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
            formatFromToPeriod(monthly.period)
        } else {
            val month = monthly.period.from
            val thisYear = month.year == dateNowLocal().year
            val pattern = if (thisYear) "MMM" else "MMM. yyyy"
            month.format(pattern)
        }

    private fun formatFromToPeriod(period: Period.FromTo): String {
        fun format(time: LocalDateTime, currentYear: Int): String =
            time.format(if (time.year == currentYear) "MMM dd" else "MMM dd, yyyy")

        val currentYear = dateNowLocal().year
        val from = format(period.from, currentYear)
        val to = format(period.to, currentYear)
        return "$from - $to"
    }

    private fun periodUi(selectedPeriod: SelectedPeriod): PeriodUi {
        fun format(time: LocalDateTime, currentYear: Int): String =
            time.format(if (time.year == currentYear) "MMM. dd" else "MMM. dd, yyyy")

        val period = selectedPeriod.period()
        val currentYear = dateNowLocal().year

        return PeriodUi(
            period = period,
            fromText = format(period.from, currentYear),
            toText = format(period.to, currentYear)
        )
    }
}