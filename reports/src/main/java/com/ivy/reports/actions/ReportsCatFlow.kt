package com.ivy.reports.actions

import com.ivy.core.action.FlowAction
import com.ivy.core.action.category.CategoriesFlow
import com.ivy.reports.data.ReportsCatType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ReportsCatFlow @Inject constructor(
    private val categoriesFlow: CategoriesFlow
) : FlowAction<Unit, List<ReportsCatType>>() {

    override fun Unit.createFlow(): Flow<List<ReportsCatType>> =
        combine(categoriesFlow(), flowOf(ReportsCatType.None)) { allCategories, noneCategory ->
            val catList: List<ReportsCatType> =
                allCategories.map { c -> ReportsCatType.Cat(cat = c) }

            listOf(noneCategory) + catList
        }
}