package com.ivy.core.data.optimized

import com.ivy.core.data.AccountId
import com.ivy.core.data.AccountValue
import com.ivy.core.data.CategoryId
import com.ivy.core.data.TagId
import com.ivy.core.data.common.Value
import java.time.LocalDateTime
import java.util.*

sealed interface PartialTransaction {
    val id: UUID
    val fee: Value?
    val time: LocalDateTime
    val category: CategoryId?
    val tags: List<TagId>
    val title: String?
    val description: String?
    val autoAdded: Boolean

    data class Income(
        override val id: UUID,
        val account: AccountId,
        val amount: Value,
        override val fee: Value?,
        override val time: LocalDateTime,
        override val category: CategoryId?,
        override val tags: List<TagId>,
        override val title: String?,
        override val description: String?,
        override val autoAdded: Boolean,
    ) : PartialTransaction

    data class Expense(
        override val id: UUID,
        val account: AccountId,
        val amount: Value,
        override val fee: Value?,
        override val time: LocalDateTime,
        override val category: CategoryId?,
        override val tags: List<TagId>,
        override val title: String?,
        override val description: String?,
        override val autoAdded: Boolean,
    ) : PartialTransaction

    data class Transfer(
        override val id: UUID,
        val from: AccountValue,
        val to: AccountValue,
        override val fee: Value?,
        override val time: LocalDateTime,
        override val category: CategoryId?,
        override val tags: List<TagId>,
        override val title: String?,
        override val description: String?,
        override val autoAdded: Boolean,
    ) : PartialTransaction
}