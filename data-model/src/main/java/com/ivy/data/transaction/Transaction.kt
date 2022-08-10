package com.ivy.data.transaction

import com.ivy.data.Account
import com.ivy.data.Category
import java.util.*

data class Transaction(
    val account: Account,
    val type: TransactionType,
    val amount: Double,
    val category: Category?,
    val time: TrnTime,

    val transfer: TransferInfo?,

    val title: String?,
    val description: String?,

    val attachmentUrl: String?,

    val metadata: TrnMetadata,

    val id: UUID
)