package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.FormattedValue

@Immutable
data class DueSectionUi(
    val dueType: DueSectionUiType,
    val income: FormattedValue?,
    val expense: FormattedValue?,
    val trns: List<TransactionUi>,
)

@Immutable
enum class DueSectionUiType {
    Upcoming, Overdue
}

fun dummyDueSectionUi(
    dueType: DueSectionUiType,
    income: FormattedValue?,
    expense: FormattedValue?,
    trns: List<TransactionUi> = emptyList()
) = DueSectionUi(
    dueType = dueType, income = income, expense = expense, trns = trns
)