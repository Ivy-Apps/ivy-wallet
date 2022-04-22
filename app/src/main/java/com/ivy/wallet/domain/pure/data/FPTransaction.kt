package com.ivy.wallet.domain.pure.data

import arrow.core.Option
import arrow.core.toOption
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Transaction
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class FPTransaction(
    val id: UUID,
    val type: TransactionType,
    val accountId: UUID,
    val categoryId: Option<UUID>,
    val amount: BigDecimal,
    val toAccountId: Option<UUID>,
    val toAmount: BigDecimal,
    val dateTime: Option<LocalDateTime>,

    val loanId: Option<UUID>,
    val loanRecordId:Option<UUID>,

    val title: Option<String>,
    val description: Option<String>,
    val dueDate: Option<LocalDateTime>,
    val recurringRuleId: Option<UUID>
)

fun Transaction.toFPTransaction(): FPTransaction =
    FPTransaction(
        id = id,
        accountId = accountId,
        type = type,
        amount = amount.toBigDecimal(),
        toAccountId = toAccountId.toOption(),
        toAmount = toAmount?.toBigDecimal() ?: amount.toBigDecimal(),
        title = title.toOption(),
        description = description.toOption(),
        dateTime = dateTime.toOption(),
        categoryId = categoryId.toOption(),
        dueDate = dueDate.toOption(),
        recurringRuleId = recurringRuleId.toOption(),
        loanId = loanId.toOption(),
        loanRecordId = loanRecordId.toOption()
    )
