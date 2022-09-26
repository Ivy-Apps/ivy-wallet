package com.ivy.core.ui.action.mapping

import android.content.Context
import androidx.annotation.StringRes
import com.ivy.common.dateNowLocal
import com.ivy.common.formatPattern
import com.ivy.core.ui.R
import com.ivy.core.ui.data.period.MonthUi
import com.ivy.core.ui.data.period.SelectedPeriodUi
import com.ivy.data.time.Period
import com.ivy.data.time.SelectedPeriod
import com.ivy.data.time.TimeUnit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MapSelectedPeriodAct @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
) : MapUiAction<SelectedPeriod, SelectedPeriodUi>() {
    override suspend fun transform(domain: SelectedPeriod): SelectedPeriodUi = when (domain) {
        is SelectedPeriod.AllTime -> SelectedPeriodUi.AllTime(
            text = appContext.getString(R.string.all_time)
        )
        is SelectedPeriod.CustomRange -> SelectedPeriodUi.CustomRange(
            text = formatFromToPeriod(domain.period),
            period = domain.period,
        )
        is SelectedPeriod.InTheLast -> SelectedPeriodUi.InTheLast(
            text = formatInTheLast(domain),
            n = domain.n,
            unit = domain.unit,
        )
        is SelectedPeriod.Monthly -> SelectedPeriodUi.Monthly(
            text = formatMonthly(domain),
            month = MonthUi(
                number = domain.month.number,
                year = domain.month.year
            )
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
            val pattern = if (thisYear) "MMM" else "MMM yyyy"
            month.formatPattern(pattern)
        }

    private fun formatFromToPeriod(period: Period.FromTo): String {
        val pattern = "MMM dd"
        val from = period.from.formatPattern(pattern)
        val to = period.to.formatPattern(pattern)
        return "$from - $to"
    }
}