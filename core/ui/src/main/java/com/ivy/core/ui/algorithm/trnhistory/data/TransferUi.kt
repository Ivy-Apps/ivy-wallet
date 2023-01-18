package com.ivy.core.ui.algorithm.trnhistory.data

import androidx.compose.runtime.Immutable
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.transaction.TrnTimeUi

@Immutable
data class TransferUi(
    val batchId: String,
    val fromAmount: ValueUi,
    val fromAccount: AccountUi,
    val toAccount: AccountUi,
    val toAmount: ValueUi?,
    val fee: ValueUi?,
    val time: TrnTimeUi,
    val category: CategoryUi?,
    val title: String?,
    val description: String?,
) : TrnListItemUi