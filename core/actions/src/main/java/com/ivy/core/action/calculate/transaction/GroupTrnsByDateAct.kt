package com.ivy.core.action.calculate.transaction

import com.ivy.common.toEpochSeconds
import com.ivy.core.action.calculate.CalculateAct
import com.ivy.core.action.currency.BaseCurrencyAct
import com.ivy.data.transaction.DateDivider
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnHistoryItem
import com.ivy.data.transaction.TrnTime
import com.ivy.frp.action.FPAction
import javax.inject.Inject

class GroupTrnsByDateAct @Inject constructor(
    private val calculateAct: CalculateAct,
    private val baseCurrencyAct: BaseCurrencyAct
) : FPAction<List<Transaction>, List<TrnHistoryItem>>() {

    override suspend fun List<Transaction>.compose(): suspend () -> List<TrnHistoryItem> = {
        groupTransactionsByDate(this)
    }

    private suspend fun groupTransactionsByDate(
        transactions: List<Transaction>,
    ): List<TrnHistoryItem> {
        if (transactions.isEmpty()) return emptyList()

        val baseCurrency = baseCurrencyAct(Unit)

        return transactions
            .groupBy { (it.time as? TrnTime.Actual)?.actual?.toLocalDate() }
            .filterKeys { it != null }
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

                listOf<TrnHistoryItem>(
                    TrnHistoryItem.Divider(
                        DateDivider(
                            date = date!!,
                            income = stats.income,
                            expense = stats.expense
                        )
                    )
                ).plus(
                    trnsForDate.map(TrnHistoryItem::Trn)
                )
            }
    }
}