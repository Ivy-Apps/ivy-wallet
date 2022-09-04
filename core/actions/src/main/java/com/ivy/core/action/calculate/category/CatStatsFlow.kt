package com.ivy.core.action.calculate.category

import com.ivy.core.action.FlowAction
import com.ivy.core.action.calculate.CalculateAct
import com.ivy.core.action.calculate.Stats
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.action.transaction.TrnsFlow
import com.ivy.core.functions.transaction.TrnWhere.ActualBetween
import com.ivy.core.functions.transaction.TrnWhere.ByCategory
import com.ivy.core.functions.transaction.and
import com.ivy.data.category.Category
import com.ivy.data.time.Period
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class CatStatsFlow @Inject constructor(
    private val calculateAct: CalculateAct,
    private val trnsFlow: TrnsFlow,
    private val baseCurrencyFlow: BaseCurrencyFlow,
) : FlowAction<CatStatsFlow.Input, Stats>() {
    data class Input(
        val period: Period,
        val category: Category?
    )

    override fun Input.createFlow(): Flow<Stats> =
        combine(categoryTransactions(), baseCurrencyFlow()) { trns, baseCurrency ->
            calculateAct(CalculateAct.Input(trns = trns, outputCurrency = baseCurrency))
        }

    private fun Input.categoryTransactions() =
        trnsFlow(ByCategory(category) and ActualBetween(period))
}