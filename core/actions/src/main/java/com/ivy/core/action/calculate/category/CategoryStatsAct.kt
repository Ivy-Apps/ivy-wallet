package com.ivy.core.action.calculate.category

import com.ivy.core.action.calculate.Stats
import com.ivy.core.action.calculate.StatsAct
import com.ivy.core.action.currency.BaseCurrencyAct
import com.ivy.core.action.transaction.read.TrnsAct
import com.ivy.data.Period
import com.ivy.data.category.Category
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.frp.then
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class CategoryStatsAct @Inject constructor(
    private val statsAct: StatsAct,
    private val trnsAct: TrnsAct,
    private val transactionDao: TransactionDao,
    private val baseCurrencyAct: BaseCurrencyAct
) : FPAction<CategoryStatsAct.Input, Stats>() {
    data class Input(
        val period: Period,
        val category: Category
    )

    override suspend fun Input.compose(): suspend () -> Stats =
        TrnsAct.Input(
            period = period,
            query = { from, to ->
                transactionDao.findAllByCategoryAndBetween(
                    categoryId = category.id,
                    startDate = from,
                    endDate = to
                )
            }
        ) asParamTo trnsAct then { trns ->
            StatsAct.Input(
                trns = trns,
                outputCurrency = baseCurrencyAct(Unit)
            )
        } then statsAct
}