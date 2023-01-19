package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi

@Deprecated("inefficient and old")
@Immutable
data class DueSectionUi(
    val dueType: DueSectionUiType,
    val income: ValueUi?,
    val expense: ValueUi?,
    val trns: List<TrnListItemUi>,
)

@Deprecated("inefficient and old")
@Immutable
enum class DueSectionUiType {
    Upcoming, Overdue
}

@Deprecated("inefficient and old")
fun dummyDueSectionUi(
    dueType: DueSectionUiType,
    income: ValueUi?,
    expense: ValueUi?,
    trns: List<TrnListItemUi> = emptyList()
) = DueSectionUi(
    dueType = dueType, income = income, expense = expense, trns = trns
)