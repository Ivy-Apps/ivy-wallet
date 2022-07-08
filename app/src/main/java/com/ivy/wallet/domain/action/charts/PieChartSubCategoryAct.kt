package com.ivy.wallet.domain.action.charts

import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.wallet.ui.statistic.level1.CategoryAmount
import javax.inject.Inject

class PieChartSubCategoryAct @Inject constructor() :
    FPAction<List<CategoryAmount>, List<CategoryAmount>>() {
    override suspend fun List<CategoryAmount>.compose(): suspend () -> List<CategoryAmount> =
        suspend {
            this
        } then { list ->
            list.groupBy {
                it.category?.parentCategoryId ?: it.category?.id
            }
        } then {
            val categoriesSet = this.toHashSet()
            it.mapKeys { mapEntry ->
                categoriesSet.find { cat -> mapEntry.key == cat.category?.id } ?: CategoryAmount(
                    null,
                    0.0
                )
            }
        } then {
            it.map { mapEntry ->
                val subCatList = mapEntry.value.filter { catAmt -> catAmt != mapEntry.key }
                val subCatTotalAmount = subCatList.sumOf { sc -> sc.amount }

                mapEntry.key.copy(
                    amount = mapEntry.key.amount + subCatTotalAmount,
                    subCategoryState = CategoryAmount.SubCategoryState(
                        subCategoriesList = subCatList,
                        subCategoryTotalAmount = subCatTotalAmount
                    )
                )
            }
        } then {
            it.sortedByDescending { cat -> cat.amount }
        }
}