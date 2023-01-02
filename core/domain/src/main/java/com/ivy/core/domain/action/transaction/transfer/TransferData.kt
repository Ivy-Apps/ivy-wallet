package com.ivy.core.domain.action.transaction.transfer

import com.ivy.data.Value
import com.ivy.data.transaction.TrnTime

data class TransferData(
    val amountFrom: Value,
    val amountTo: Value,
    val accountFromId: String,
    val accountToId: String,
    val categoryId: String?,
    val time: TrnTime,
    val title: String?,
    val description: String?,
    val fee: Value?,
)