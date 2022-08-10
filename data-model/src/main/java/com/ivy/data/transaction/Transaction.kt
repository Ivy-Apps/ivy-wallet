package com.ivy.data.transaction

import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import java.util.*

data class Transaction(
    val account: AccountOld,
    val type: TransactionType,
    val amount: Double,
    val category: CategoryOld?,
    val time: TrnTime,

    val transfer: TransferInfo?,

    val title: String?,
    val description: String?,

    val attachmentUrl: String?,

    val metadata: TrnMetadata,

    val id: UUID
)