package com.ivy.pie_charts.action

import com.ivy.pie_charts.model.CategoryAmount
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.frp.thenInvokeAfter
import java.util.*
import javax.inject.Inject

class SubCategoryAct @Inject constructor() :
    FPAction<List<CategoryAmount>, List<CategoryAmount>>() {

    override suspend fun List<CategoryAmount>.compose(): suspend () -> List<CategoryAmount> =
        suspend { this } then
                ::groupByParentCategoryID then
                ::mapParentCategoryIdToCategoryAmount then
                ::flattenToList

    private fun groupByParentCategoryID(
        categoryAmounts: List<CategoryAmount>
    ): Pair<List<CategoryAmount>, Map<UUID?, List<CategoryAmount>>> {
        val groupedMap = categoryAmounts.groupBy {
            it.category?.parentCategoryId ?: it.category?.id
        }

        return Pair(categoryAmounts, groupedMap)
    }

    private fun mapParentCategoryIdToCategoryAmount(
        pair: Pair<List<CategoryAmount>, Map<UUID?, List<CategoryAmount>>>
    ): Map<CategoryAmount, List<CategoryAmount>> {
        val categoriesSet = pair.first.toHashSet()
        val groupedMap = pair.second

        return groupedMap.mapKeys { mapEntry ->
            categoriesSet.find { cat -> mapEntry.key == cat.category?.id } ?: CategoryAmount(
                null,
                0.0
            )
        }
    }

    private suspend fun flattenToList(
        groupedMap: Map<CategoryAmount, List<CategoryAmount>>
    ): List<CategoryAmount> = suspend {
        groupedMap.map { mapEntry ->
            val parentCategory = mapEntry.key
            val subCatList = mapEntry.value.filter { subCategory -> subCategory != parentCategory }
                .filter { sc -> sc.totalAmount() != 0.0 }
            val subCatTotalAmount = subCatList.sumOf { subCategory -> subCategory.amount }

            parentCategory.copy(
                amount = parentCategory.amount,
                subCategoryState = CategoryAmount.SubCategoryState(
                    subCategoriesList = subCatList,
                    subCategoryTotalAmount = subCatTotalAmount,
                    subCategoryListExpanded = false
                )
            )
        }
    } then {
        it.sortedByDescending { categoryAmount -> categoryAmount.totalAmount() }
    } thenInvokeAfter {
        it.filter { categoryAmount -> categoryAmount.totalAmount() != 0.0 }
    }
}