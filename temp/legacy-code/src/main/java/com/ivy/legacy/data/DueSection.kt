package com.ivy.legacy.data

import androidx.compose.runtime.Immutable
import com.ivy.base.legacy.Transaction
import com.ivy.wallet.domain.pure.data.IncomeExpensePair
import kotlinx.collections.immutable.ImmutableList

@Deprecated("Uses legacy Transaction")
@Immutable
data class LegacyDueSection(
    val trns: ImmutableList<Transaction>,
    val expanded: Boolean,
    val stats: IncomeExpensePair
)

@Deprecated("Legacy data model")
@Immutable
data class DueSection(
    val trns: ImmutableList<com.ivy.data.model.Transaction>,
    val expanded: Boolean,
    val stats: IncomeExpensePair
)