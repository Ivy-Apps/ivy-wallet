package com.ivy.data.transaction

import com.ivy.data.account.Account
import com.ivy.data.category.Category
import java.util.*

// TODO: Transaction should have currency and not depend on account
// TODO: Replace "amount: Double" with "value: TrnValue"
// data class TrnValue(val amount: Double, val currency: CurrencyCode)
data class Transaction(
    val id: UUID,

    val account: Account,
    val type: TransactionType,
    val amount: Double,
    val category: Category?,
    val time: TrnTime,

    val title: String?,
    val description: String?,

    val attachmentUrl: String?,

    val metadata: TrnMetadata,
)