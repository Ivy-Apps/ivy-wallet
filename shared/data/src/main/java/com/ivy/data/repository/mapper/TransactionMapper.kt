package com.ivy.data.repository.mapper

import arrow.core.Either
import arrow.core.raise.either
import com.ivy.base.model.TransactionType
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.TransactionId
import com.ivy.data.model.TransactionMetadata
import com.ivy.data.model.Transfer
import com.ivy.data.model.common.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.PositiveDouble
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class TransactionMapper @Inject constructor() {

    fun TransactionEntity.toDomain(
        accountAssetCode: AssetCode?,
        fromAccountAssetCode: AssetCode? = null
    ): Either<String, Transaction> =
        either {
            when (this@toDomain.type) {
                TransactionType.INCOME -> toIncomeModel(accountAssetCode).bind()
                TransactionType.EXPENSE -> toExpenseModel(accountAssetCode).bind()
                TransactionType.TRANSFER -> toTransferModel(
                    fromAccountAssetCode = fromAccountAssetCode,
                    toAccountAssetCode = accountAssetCode
                ).bind()
            }
        }

    fun Transaction.toEntity(accountId: AccountId): TransactionEntity {
        return when (this) {
            is Expense -> toEntity(accountId)
            is Income -> toEntity(accountId)
            is Transfer -> toEntity()
        }
    }

    private fun Expense.toEntity(accountId: AccountId): TransactionEntity {
        return TransactionEntity(
            accountId = accountId.value,
            type = TransactionType.EXPENSE,
            amount = value.amount.value,
            toAccountId = null,
            toAmount = null,
            title = title?.value,
            description = description?.value,
            dateTime = LocalDateTime.from(time),
            categoryId = category?.value,
            dueDate = null,
            recurringRuleId = metadata.recurringRuleId,
            attachmentUrl = null,
            loanId = metadata.loanId,
            loanRecordId = metadata.loanRecordId,
            isSynced = true,
            isDeleted = removed,
            id = id.value
        )
    }

    private fun Income.toEntity(accountId: AccountId): TransactionEntity {
        return TransactionEntity(
            accountId = accountId.value,
            type = TransactionType.INCOME,
            amount = value.amount.value,
            toAccountId = null,
            toAmount = null,
            title = title?.value,
            description = description?.value,
            dateTime = LocalDateTime.from(time),
            categoryId = category?.value,
            dueDate = null,
            recurringRuleId = metadata.recurringRuleId,
            attachmentUrl = null,
            loanId = metadata.loanId,
            loanRecordId = metadata.loanRecordId,
            isSynced = true,
            isDeleted = removed,
            id = id.value
        )
    }

    private fun Transfer.toEntity(): TransactionEntity {
        return TransactionEntity(
            accountId = fromAccount.value,
            type = TransactionType.TRANSFER,
            amount = fromValue.amount.value,
            toAccountId = toAccount.value,
            toAmount = toValue.amount.value,
            title = title?.value,
            description = description?.value,
            dateTime = LocalDateTime.from(time),
            categoryId = category?.value,
            dueDate = null,
            recurringRuleId = metadata.recurringRuleId,
            attachmentUrl = null,
            loanId = metadata.loanId,
            loanRecordId = metadata.loanRecordId,
            isSynced = true,
            isDeleted = removed,
            id = id.value
        )
    }

    private fun TransactionEntity.toIncomeModel(accountAssetCode: AssetCode?): Either<String, Income> {
        return either {
            Income(
                id = TransactionId(id),
                title = title?.let { NotBlankTrimmedString(it) },
                description = description?.let { NotBlankTrimmedString(it) },
                category = categoryId?.let { CategoryId(it) },
                time = Instant.from(dateTime),
                settled = false,
                metadata = TransactionMetadata(
                    recurringRuleId = recurringRuleId ?: UUID.randomUUID(),
                    loanId = loanId,
                    loanRecordId = loanRecordId ?: UUID.randomUUID()
                ),
                lastUpdated = Instant.from(dateTime),
                removed = isDeleted,
                value = Value(
                    amount = PositiveDouble(amount),
                    asset = accountAssetCode ?: AssetCode.from("").bind()
                )
            )
        }
    }

    private fun TransactionEntity.toExpenseModel(accountAssetCode: AssetCode?): Either<String, Expense> {
        return either {
            Expense(
                id = TransactionId(id),
                title = title?.let { NotBlankTrimmedString(it) },
                description = description?.let { NotBlankTrimmedString(it) },
                category = categoryId?.let { CategoryId(it) },
                time = Instant.from(dateTime),
                settled = false,
                metadata = TransactionMetadata(
                    recurringRuleId = recurringRuleId ?: UUID.randomUUID(),
                    loanId = loanId,
                    loanRecordId = loanRecordId ?: UUID.randomUUID()
                ),
                lastUpdated = Instant.from(dateTime),
                removed = isDeleted,
                value = Value(
                    amount = PositiveDouble(amount),
                    asset = accountAssetCode ?: AssetCode.from("").bind()
                )
            )
        }
    }

    private fun TransactionEntity.toTransferModel(
        fromAccountAssetCode: AssetCode?,
        toAccountAssetCode: AssetCode?
    ): Either<String, Transfer> {
        return either {
            Transfer(
                id = TransactionId(id),
                title = title?.let { NotBlankTrimmedString(it) },
                description = description?.let { NotBlankTrimmedString(it) },
                category = categoryId?.let { CategoryId(it) },
                time = Instant.from(dateTime),
                settled = false,
                metadata = TransactionMetadata(
                    recurringRuleId = recurringRuleId ?: UUID.randomUUID(),
                    loanId = loanId,
                    loanRecordId = loanRecordId ?: UUID.randomUUID()
                ),
                lastUpdated = Instant.from(dateTime),
                removed = isDeleted,
                fromAccount = AccountId(accountId),
                fromValue = Value(
                    amount = PositiveDouble(amount),
                    asset = fromAccountAssetCode ?: AssetCode.from("").bind()
                ),
                toAccount = AccountId(toAccountId!!),
                toValue = Value(
                    amount = PositiveDouble(toAmount!!),
                    asset = toAccountAssetCode ?: AssetCode.from("").bind()
                )
            )
        }
    }
}