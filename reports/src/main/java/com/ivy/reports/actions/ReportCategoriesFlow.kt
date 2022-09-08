package com.ivy.reports.actions

import com.ivy.core.action.FlowAction
import com.ivy.core.action.category.CategoriesFlow
import com.ivy.reports.data.ReportCategoryType
import com.ivy.reports.data.SelectableReportsCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ReportCategoriesFlow @Inject constructor(
    private val categoriesFlow: CategoriesFlow
) : FlowAction<Unit, List<SelectableReportsCategory>>() {

    override fun Unit.createFlow(): Flow<List<SelectableReportsCategory>> =
        combine(categoriesFlow(), flowOf(ReportCategoryType.None)) { allCategories, noneCategory ->
            val catList: List<ReportCategoryType> =
                allCategories.map { c -> ReportCategoryType.Cat(cat = c) }

            (listOf(noneCategory) + catList).map { SelectableReportsCategory(it) }
        }
}