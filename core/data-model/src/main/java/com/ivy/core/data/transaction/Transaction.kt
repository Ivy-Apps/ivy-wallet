package com.ivy.core.data.transaction

import com.ivy.core.data.AttachmentId
import com.ivy.core.data.Value
import com.ivy.core.data.attribute.AccountId
import com.ivy.core.data.attribute.CategoryId
import com.ivy.core.data.attribute.TagId
import java.time.LocalDateTime
import java.util.*

sealed interface Transaction {
    val id: UUID
    val fee: Value?
    val time: TransactionTime
    val category: CategoryId?
    val tags: List<TagId>
    val attachments: List<AttachmentId>
    val title: String?
    val description: String?
    val hidden: Boolean

    data class Income(
        override val id: UUID,
        val account: AccountId,
        val amount: Value,
        override val fee: Value?,
        override val time: TransactionTime,
        override val category: CategoryId?,
        override val tags: List<TagId>,
        override val attachments: List<AttachmentId>,
        override val title: String?,
        override val description: String?,
        override val hidden: Boolean
    ) : Transaction

    data class Expense(
        override val id: UUID,
        val account: AccountId,
        val amount: Value,
        override val fee: Value?,
        override val time: TransactionTime,
        override val category: CategoryId?,
        override val tags: List<TagId>,
        override val attachments: List<AttachmentId>,
        override val title: String?,
        override val description: String?,
        override val hidden: Boolean
    ) : Transaction

    data class Transfer(
        override val id: UUID,
        val from: AccountValue,
        val to: AccountValue,
        override val fee: Value?,
        override val time: TransactionTime,
        override val category: CategoryId?,
        override val tags: List<TagId>,
        override val attachments: List<AttachmentId>,
        override val title: String?,
        override val description: String?,
        override val hidden: Boolean
    ) : Transaction
}

sealed interface TransactionTime {
    val time: LocalDateTime

    data class Actual(override val time: LocalDateTime) : TransactionTime
    data class Due(override val time: LocalDateTime) : TransactionTime
}

data class AccountValue(
    val account: AccountId,
    val value: Value,
)