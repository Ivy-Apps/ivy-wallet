package com.ivy.data.model

import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.sync.Identifiable
import com.ivy.data.model.sync.UniqueId
import java.time.Instant
import java.util.UUID

@JvmInline
value class TransactionId(override val value: UUID) : UniqueId

sealed interface Transaction : Identifiable<TransactionId> {
    override val id: TransactionId
    val title: NotBlankTrimmedString?
    val description: NotBlankTrimmedString?
    val category: CategoryId?
    val time: Instant
    val settled: Boolean
    val metadata: TransactionMetadata

    // TODO: Get rid of Tags from the core model because of perf. and complexity
    val tags: List<TagId>
}

data class Income(
    override val id: TransactionId,
    override val title: NotBlankTrimmedString?,
    override val description: NotBlankTrimmedString?,
    override val category: CategoryId?,
    override val time: Instant,
    override val settled: Boolean,
    override val metadata: TransactionMetadata,
    override val tags: List<TagId>,
    val value: PositiveValue,
    val account: AccountId,
) : Transaction

data class Expense(
    override val id: TransactionId,
    override val title: NotBlankTrimmedString?,
    override val description: NotBlankTrimmedString?,
    override val category: CategoryId?,
    override val time: Instant,
    override val settled: Boolean,
    override val metadata: TransactionMetadata,
    override val tags: List<TagId>,
    val value: PositiveValue,
    val account: AccountId,
) : Transaction

data class Transfer(
    override val id: TransactionId,
    override val title: NotBlankTrimmedString?,
    override val description: NotBlankTrimmedString?,
    override val category: CategoryId?,
    override val time: Instant,
    override val settled: Boolean,
    override val metadata: TransactionMetadata,
    override val tags: List<TagId>,
    val fromAccount: AccountId,
    val fromValue: PositiveValue,
    val toAccount: AccountId,
    val toValue: PositiveValue,
) : Transaction

@Suppress("DataClassTypedIDs")
data class TransactionMetadata(
    val recurringRuleId: UUID?,
    val paidForDateTime: Instant?,
    // This refers to the loan id that is linked with a transaction
    val loanId: UUID? = null,
    // This refers to the loan record id that is linked with a transaction
    val loanRecordId: UUID?,
)

fun Transaction.getFromValue(): PositiveValue = when (this) {
    is Expense -> value
    is Income -> value
    is Transfer -> fromValue
}

fun Transaction.getFromAccount(): AccountId = when (this) {
    is Expense -> account
    is Income -> account
    is Transfer -> fromAccount
}

fun Transaction.getToAccount(): AccountId? = when (this) {
    is Expense -> null
    is Income -> null
    is Transfer -> toAccount
}
