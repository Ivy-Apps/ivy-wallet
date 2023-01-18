package com.ivy.core.ui.algorithm.trnhistory.data

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi

@Immutable
data class DueDividerUi(
    val id: String, // a unique string used for collapse/expanded purposes
    val income: ValueUi?,
    val expense: ValueUi?,
    val label: String,
    val collapsed: Boolean
) : TrnListItemUi