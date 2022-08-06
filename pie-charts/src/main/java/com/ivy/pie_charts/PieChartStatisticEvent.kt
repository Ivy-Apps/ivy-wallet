package com.ivy.pie_charts

import com.ivy.pie_charts.model.CategoryAmount
import com.ivy.base.TimePeriod
import com.ivy.data.Category
import com.ivy.screens.PieChartStatistic

sealed class PieChartStatisticEvent {
    data class Start(val screen: PieChartStatistic) : PieChartStatisticEvent()
    object OnSelectNextMonth : PieChartStatisticEvent()

    object OnSelectPreviousMonth : PieChartStatisticEvent()

    data class OnSetPeriod(val timePeriod: TimePeriod) : PieChartStatisticEvent()

    data class OnCategoryClicked(val category: Category?) :
        PieChartStatisticEvent()

    data class OnShowMonthModal(val timePeriod: TimePeriod?) : PieChartStatisticEvent()

    data class OnUnpackSubCategories(val unpackAllSubCategories: Boolean) : PieChartStatisticEvent()

    data class OnSubCategoryListExpanded(
        val parentCategoryAmount: CategoryAmount,
        val expandedState: Boolean
    ) : PieChartStatisticEvent()
}