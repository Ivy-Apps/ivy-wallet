package com.ivy.core.domain.action.calculate.transaction

import com.ivy.common.timeNowUTC
import com.ivy.common.toEpochSeconds
import com.ivy.core.action.calculate.CalculateAct
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.persistence.query.actual
import com.ivy.core.persistence.query.overdue
import com.ivy.core.persistence.query.upcoming
import com.ivy.data.CurrencyCode
import com.ivy.data.transaction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GroupTrnsFlow @Inject constructor(
    private val calculateAct: CalculateAct,
    private val baseCurrencyFlow: BaseCurrencyFlow
) : com.ivy.core.domain.action.FlowAction<List<Transaction>, TransactionsList>() {

    override fun List<Transaction>.createFlow(): Flow<TransactionsList> =
        baseCurrencyFlow()
            .map { baseCurrency ->
                val dueRes = groupDue(trns = this, baseCurrency = baseCurrency)
                val historyGrouped =
                    groupByDate(actualTrns = dueRes.actualTrns, baseCurrency = baseCurrency)
                TransactionsList(
                    upcoming = dueRes.upcomingSection,
                    overdue = dueRes.overdueSection,
                    history = historyGrouped
                )
            }.flowOn(Dispatchers.Default)

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