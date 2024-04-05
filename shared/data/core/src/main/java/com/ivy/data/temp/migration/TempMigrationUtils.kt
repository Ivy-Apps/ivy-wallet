package com.ivy.data.temp.migration

import com.ivy.base.model.TransactionType
import com.ivy.data.model.AccountId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

fun com.ivy.data.model.Transaction.getValue(): BigDecimal =
    when (this) {
        is com.ivy.data.model.Expense -> value.amount.value.toBigDecimal()
        is com.ivy.data.model.Income -> value.amount.value.toBigDecimal()
        is com.ivy.data.model.Transfer -> fromValue.amount.value.toBigDecimal()
    }

fun com.ivy.data.model.Transaction.getAccountId(): UUID =
    when (this) {
        is com.ivy.data.model.Expense -> account.value
        is com.ivy.data.model.Income -> account.value
        is com.ivy.data.model.Transfer -> fromAccount.value
    }

fun com.ivy.data.model.Transaction.getAccount(): com.ivy.data.model.AccountId = when (this) {
    is com.ivy.data.model.Expense -> account
    is com.ivy.data.model.Income -> account
    is com.ivy.data.model.Transfer -> fromAccount
}

fun com.ivy.data.model.Transaction.getTransactionType(): TransactionType {
    return when (this) {
        is com.ivy.data.model.Expense -> TransactionType.EXPENSE
        is com.ivy.data.model.Income -> TransactionType.INCOME
        is com.ivy.data.model.Transfer -> TransactionType.TRANSFER
    }
}

fun com.ivy.data.model.Transaction.settleNow(): com.ivy.data.model.Transaction {
    val timeNow = Instant.now()
    return when (this) {
        is com.ivy.data.model.Income -> this.copy(
            settled = true,
            time = timeNow
        )

        is com.ivy.data.model.Expense -> this.copy(
            settled = true,
            time = timeNow
        )

        is com.ivy.data.model.Transfer -> this.copy(
            settled = true,
            time = timeNow
        )
    }
}