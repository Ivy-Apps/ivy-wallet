package com.ivy.core.ui.algorithm.trnhistory.data

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.dummyValueUi
import java.util.*

@Immutable
data class DueDividerUi(
    val id: String, // a unique string used for collapse/expanded purposes
    val income: ValueUi?,
    val expense: ValueUi?,
    val label: String,
    val type: DueDividerUiType,
    val collapsed: Boolean
) : TrnListItemUi

@Immutable
enum class DueDividerUiType {
    Upcoming, Overdue
}

fun dummyDueDividerUi(
    id: String = UUID.randomUUID()
        .toString(), // a unique string used for collapse/expanded purposes
    income: ValueUi? = dummyValueUi(),
    expense: ValueUi? = dummyValueUi(),
    label: String = "Upcoming",
    type: DueDividerUiType = DueDividerUiType.Upcoming,
    collapsed: Boolean = false,
) = DueDividerUi(
    id, income, expense, label, type, collapsed
)