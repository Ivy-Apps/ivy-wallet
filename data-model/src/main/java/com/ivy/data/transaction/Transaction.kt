package com.ivy.data.transaction

import com.ivy.data.account.Account
import com.ivy.data.category.Category
import java.util.*

data class Transaction(
    val id: UUID,

    val account: Account,
    val type: TransactionType,
    val value: Value,
    val category: Category?,
    val time: TrnTime,

    val title: String?,
    val description: String?,

    val attachmentUrl: String?,

    val metadata: TrnMetadata,
)