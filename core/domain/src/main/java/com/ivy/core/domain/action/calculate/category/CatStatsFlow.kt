package com.ivy.core.domain.action.calculate.category

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.calculate.CalculateFlow
import com.ivy.core.domain.action.calculate.Stats
import com.ivy.core.domain.action.transaction.TrnQuery.ActualBetween
import com.ivy.core.domain.action.transaction.TrnQuery.ByCategoryId
import com.ivy.core.domain.action.transaction.TrnsFlow
import com.ivy.core.domain.action.transaction.and
import com.ivy.data.CurrencyCode
import com.ivy.data.category.Category
import com.ivy.data.time.Period
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import javax.inject.Inject

/**
 * Calculates category's incomes and expenses **excluding transfers and hidden transactions**.
 */
class CatStatsFlow @Inject constructor(
    private val trnsFlow: TrnsFlow,
    private val calculateFlow: CalculateFlow,
) : FlowAction<CatStatsFlow.Input, Stats>() {
    /**
     * @param outputCurrency pass **null** for base currency
     */
    data class Input(
        val period: Period,
        val category: Category?,
        val outputCurrency: CurrencyCode? = null,
    )

    @OptIn(FlowPreview::class)
    override fun Input.createFlow(): Flow<Stats> = trnsFlow(
        ByCategoryId(categoryId = category?.id) and ActualBetween(period)
    ).flatMapMerge { trns ->
        calculateFlow(
            CalculateFlow.Input(
                trns = trns,
                outputCurrency = null,
                includeTransfers = false,
                includeHidden = false,
            )
        )
    }
}