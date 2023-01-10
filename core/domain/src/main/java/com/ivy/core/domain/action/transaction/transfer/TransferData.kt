package com.ivy.core.domain.action.transaction.transfer

import com.ivy.data.Sync
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.transaction.TrnTime

data class TransferData(
    val amountFrom: Value,
    val amountTo: Value,
    val accountFrom: Account,
    val accountTo: Account,
    val category: Category?,
    val time: TrnTime,
    val title: String?,
    val description: String?,
    val fee: Value?,
    val sync: Sync,
)