package com.ivy.core.ui.algorithm.trnhistory.data

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.SignedValueUi

@Immutable
data class DateDividerUi(
    val id: String, // a unique string used for collapse/expanded purposes
    val date: String,
    val dateContext: String,
    val cashflow: SignedValueUi,
    val collapsed: Boolean
) : TrnListItemUi