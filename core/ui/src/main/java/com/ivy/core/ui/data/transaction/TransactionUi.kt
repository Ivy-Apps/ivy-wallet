package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.FormattedValue
import com.ivy.core.ui.data.AccountUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.data.transaction.TrnType

@Immutable
data class TransactionUi(
    val id: String,
    val type: TrnType,
    val value: FormattedValue,
    val account: AccountUi,
    val category: CategoryUi?,
    val title: String?,
    val description: String?,
    val time: TrnTimeUi,
)