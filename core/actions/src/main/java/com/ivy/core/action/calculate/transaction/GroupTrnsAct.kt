package com.ivy.core.action.calculate.transaction

import com.ivy.common.timeNowUTC
import com.ivy.common.toEpochSeconds
import com.ivy.core.action.calculate.CalculateAct
import com.ivy.core.action.currency.BaseCurrencyAct
import com.ivy.core.functions.transaction.actual
import com.ivy.core.functions.transaction.overdue
import com.ivy.core.functions.transaction.upcoming
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.*
import com.ivy.frp.action.FPAction
import javax.inject.Inject

class GroupTrnsAct @Inject constructor(
    private val calculateAct: CalculateAct,
    private val baseCurrencyAct: BaseCurrencyAct
) : FPAction<List<Transaction>, TransactionsList>() {

    override suspend fun List<Transaction>.compose(): suspend () -> TransactionsList = {
        val baseCurrency = baseCurrencyAct(Unit)
        val dueRes = groupDue(trns = this, baseCurrency = baseCurrency)
        val historyGrouped =
            groupByDate(actualTrns = dueRes.actualTrns, baseCurrency = baseCurrency)
        TransactionsList(
            upcoming = dueRes.upcomingSection,
            overdue = dueRes.overdueSection,
            history = historyGrouped
        )
    }

    private suspend fun groupDue(
        trns: List<Transaction>,
        baseCurrency: CurrencyCode,
    ): GroupDueResult {
        val timeNow = timeNowUTC()
        val upcomingTrns = trns.filter { upcoming(it, timeNow) }
        val overdueTrns = trns.filter { overdue(it, timeNow) }

        val upcomingSection = if (upcomingTrns.isNotEmpty()) {
            val stats = calculateAct(
                CalculateAct.Input(
                    trns = upcomingTrns,
                    outputCurrency = baseCurrency
                )
            )
            UpcomingSection(
                income = Value(stats.income, baseCurrency),
                expense = Value(stats.expense, baseCurrency),
                trns = upcomingTrns
            )
        } else null

        val overdueSection = if (overdueTrns.isNotEmpty()) {
            val stats = calculateAct(
                CalculateAct.Input(
                    trns = overdueTrns,
                    outputCurrency = baseCurrency
                )
            )
            OverdueSection(
                income = Value(stats.income, baseCurrency),
                expense = Value(stats.expense, baseCurrency),
                trns = overdueTrns
            )
        } else null

        return GroupDueResult(
            upcomingSection = upcomingSection,
            overdueSection = overdueSection,
            actualTrns = trns.filter(::actual)
        )
    }

    private data class GroupDueResult(
        val upcomingSection: UpcomingSection?,
        val overdueSection: OverdueSection?,
        val actualTrns: List<Transaction>
    )

    private suspend fun groupByDate(
        actualTrns: List<Transaction>,
        baseCurrency: CurrencyCode,
    ): List<TrnListItem> {
        if (actualTrns.isEmpty()) return emptyList()

        return actualTrns
            .groupBy { (it.time as? TrnTime.Actual)?.actual?.toLocalDate() }
            .toSortedMap { date1, date2 ->
                if (date1 == null || date2 == null)
                    return@toSortedMap 0 //this case shouldn't happen
                (date2.atStartOfDay().toEpochSeconds() - date1.atStartOfDay()
                    .toEpochSeconds()).toInt()
            }
            .flatMap { (date, trnsForDate) ->
                val stats = calculateAct(
                    CalculateAct.Input(
                        trns = trnsForDate,
                        outputCurrency = baseCurrency,
                    )
                )

                listOf<TrnListItem>(
                    TrnListItem.DateDivider(
                        date = date!!,
                        cashflow = Value(stats.balance, baseCurrency),
                    )
                ).plus(
                    trnsForDate.map(TrnListItem::Trn)
                )
            }
    }
}