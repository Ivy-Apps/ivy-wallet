package com.ivy.core.action.calculate.category

import com.ivy.core.action.calculate.CalculateAct
import com.ivy.core.action.calculate.Stats
import com.ivy.core.action.currency.BaseCurrencyAct
import com.ivy.core.action.transaction.TrnsAct
import com.ivy.core.functions.transaction.TrnWhere.ActualBetween
import com.ivy.core.functions.transaction.TrnWhere.ByCategory
import com.ivy.core.functions.transaction.and
import com.ivy.data.category.Category
import com.ivy.data.time.Period
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import javax.inject.Inject

@Deprecated(
    message = "migrating to flows",
    replaceWith = ReplaceWith("CatStatsFlow")
)
class CatActualStatsAct @Inject constructor(
    private val calculateAct: CalculateAct,
    private val trnsAct: TrnsAct,
    private val baseCurrencyAct: BaseCurrencyAct
) : FPAction<CatActualStatsAct.Input, Stats>() {
    data class Input(
        val period: Period,
        val category: Category?
    )

    override suspend fun Input.compose(): suspend () -> Stats = {
        ByCategory(category) and ActualBetween(period)
    } then trnsAct then { trns ->
        CalculateAct.Input(
            trns = trns,
            outputCurrency = baseCurrencyAct(Unit)
        )
    } then calculateAct
}