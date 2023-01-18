package com.ivy.core.ui.algorithm.trnhistory.data

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi
import com.ivy.data.transaction.TransactionType

@Immutable
data class TransactionUi(
    val id: String,
    val amount: ValueUi,
    val time: TrnTimeUi,
    val account: AccountUi,
    val category: CategoryUi?,
    val title: String?,
    val description: String?,
    val type: TransactionType,
) : TrnListItemUi