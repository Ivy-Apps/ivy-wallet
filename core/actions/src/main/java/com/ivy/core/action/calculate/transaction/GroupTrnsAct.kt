package com.ivy.core.action.calculate.transaction

import com.ivy.common.timeNowUTC
import com.ivy.common.toEpochSeconds
import com.ivy.core.action.calculate.CalculateAct
import com.ivy.core.action.currency.BaseCurrencyAct
import com.ivy.core.functions.transaction.actual
import com.ivy.core.functions.transaction.overdue
import com.ivy.core.functions.transaction.upcoming
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnListItem
import com.ivy.data.transaction.TrnTime
import com.ivy.data.transaction.Value
import com.ivy.frp.action.FPAction
import javax.inject.Inject

class GroupTrnsAct @Inject constructor(
    private val calculateAct: CalculateAct,
    private val baseCurrencyAct: BaseCurrencyAct
) : FPAction<List<Transaction>, List<TrnListItem>>() {

    override suspend fun List<Transaction>.compose(): suspend () -> List<TrnListItem> = {
        val baseCurrency = baseCurrencyAct(Unit)
        val (dueGrouped, actualTrns) = groupDue(trns = this, baseCurrency = baseCurrency)
        val historyGrouped = groupByDate(actualTrns = actualTrns, baseCurrency = baseCurrency)
        dueGrouped.plus(historyGrouped)
    }

    private suspend fun groupDue(
        trns: List<Transaction>,
        baseCurrency: CurrencyCode,
    ): Pair<List<TrnListItem>, List<Transaction>> {
        val timeNow = timeNowUTC()
        val upcoming = trns.filter { upcoming(it, timeNow) }
        val overdue = trns.filter { overdue(it, timeNow) }
        val result = mutableListOf<TrnListItem>()

        if (upcoming.isNotEmpty()) {
            val stats = calculateAct(
                CalculateAct.Input(
                    trns = upcoming,
                    outputCurrency = baseCurrency
                )
            )
            result.add(
                TrnListItem.UpcomingSection(
                    income = Value(stats.income, baseCurrency),
                    expense = Value(stats.expense, baseCurrency),
                )
            )
            result.addAll(upcoming.map(TrnListItem::Trn))
        }

        if (overdue.isNotEmpty()) {
            val stats = calculateAct(
                CalculateAct.Input(
                    trns = overdue,
                    outputCurrency = baseCurrency
                )
            )
            result.add(
                TrnListItem.OverdueSection(
                    income = Value(stats.income, baseCurrency),
                    expense = Value(stats.expense, baseCurrency),
                )
            )
            result.addAll(overdue.map(TrnListItem::Trn))
        }

        return result to trns.filter(::actual)
    }

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