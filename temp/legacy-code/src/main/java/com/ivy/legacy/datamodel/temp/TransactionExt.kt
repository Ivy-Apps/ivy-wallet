package com.ivy.legacy.datamodel.temp

import com.ivy.base.legacy.LegacyTag
import com.ivy.base.legacy.Transaction
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.model.Tag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

fun TransactionEntity.toDomain(tags: ImmutableList<LegacyTag> = persistentListOf()): Transaction = Transaction(
    accountId = accountId,
    type = type,
    amount = amount.toBigDecimal(),
    toAccountId = toAccountId,
    toAmount = toAmount?.toBigDecimal() ?: amount.toBigDecimal(),
    title = title,
    description = description,
    dateTime = dateTime,
    categoryId = categoryId,
    dueDate = dueDate,
    recurringRuleId = recurringRuleId,
    attachmentUrl = attachmentUrl,
    loanId = loanId,
    loanRecordId = loanRecordId,
    id = id,
    tags = tags
)
fun Tag.toLegacyTag(): LegacyTag = LegacyTag(this.id.value, this.name.value)
fun List<Tag>.toImmutableLegacyTags(): ImmutableList<LegacyTag> =
    this.map { it.toLegacyTag() }.toImmutableList()

fun TransactionEntity.isIdenticalWith(transaction: TransactionEntity?): Boolean {
    if (transaction == null) return false

    // Set isSynced && isDeleted to false so they aren't accounted in the equals check
    return this.copy(
        isSynced = false,
        isDeleted = false
    ) == transaction.copy(
        isSynced = false,
        isDeleted = false
    )
}
