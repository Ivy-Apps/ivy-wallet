package com.ivy.piechart

import com.ivy.legacy.data.model.TimePeriod
import com.ivy.legacy.datamodel.Category

sealed interface PieChartStatisticEvent {
    data object OnSelectNextMonth : PieChartStatisticEvent
    data object OnSelectPreviousMonth : PieChartStatisticEvent
    data class OnSetPeriod(val timePeriod: TimePeriod) : PieChartStatisticEvent
    data class OnCategoryClicked(val category: Category?) : PieChartStatisticEvent
    data class OnShowMonthModal(val timePeriod: TimePeriod?) : PieChartStatisticEvent
}