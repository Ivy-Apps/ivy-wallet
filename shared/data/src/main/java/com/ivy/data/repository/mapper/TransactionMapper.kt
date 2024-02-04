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
import java.time.ZoneId
import javax.inject.Inject

class TransactionMapper @Inject constructor() {

    fun TransactionEntity.toDomain(
        accountAssetCode: AssetCode?,
        toAccountAssetCode: AssetCode? = null
    ): Either<String, Transaction> =
        either {
            when (this@toDomain.type) {
                TransactionType.INCOME -> toIncomeModel(accountAssetCode).bind()
                TransactionType.EXPENSE -> toExpenseModel(accountAssetCode).bind()
                TransactionType.TRANSFER -> toTransferModel(
                    fromAccountAssetCode = accountAssetCode,
                    toAccountAssetCode = toAccountAssetCode
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
            toAmount = value.amount.value,
            title = title?.value,
            description = description?.value,
            dateTime = time.atZone(ZoneId.systemDefault()).toLocalDateTime(),
            categoryId = category?.value,
            dueDate = null,
            recurringRuleId = metadata.recurringRuleId,
            attachmentUrl = null,
            loanId = metadata.loanId,
            loanRecordId = metadata.loanRecordId,
            isSynced = false,
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
            toAmount = value.amount.value,
            title = title?.value,
            description = description?.value,
            dateTime = time.atZone(ZoneId.systemDefault()).toLocalDateTime(),
            categoryId = category?.value,
            dueDate = null,
            recurringRuleId = metadata.recurringRuleId,
            attachmentUrl = null,
            loanId = metadata.loanId,
            loanRecordId = metadata.loanRecordId,
            isSynced = false,
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
            dateTime = time.atZone(ZoneId.systemDefault()).toLocalDateTime(),
            categoryId = category?.value,
            dueDate = null,
            recurringRuleId = metadata.recurringRuleId,
            attachmentUrl = null,
            loanId = metadata.loanId,
            loanRecordId = metadata.loanRecordId,
            isSynced = false,
            isDeleted = removed,
            id = id.value
        )
    }

    private fun TransactionEntity.toIncomeModel(accountAssetCode: AssetCode?): Either<String, Income> {
        return either {
            val zoneId = ZoneId.systemDefault()
            val metadata = TransactionMetadata(
                recurringRuleId = recurringRuleId,
                loanId = loanId,
                loanRecordId = loanRecordId
            )

            val value = Value(
                amount = PositiveDouble.from(amount).bind(),
                asset = accountAssetCode ?: AssetCode.from("").bind()
            )

            Income(
                id = TransactionId(id),
                title = title?.let { NotBlankTrimmedString.from(it).bind() },
                description = description?.let { NotBlankTrimmedString.from(it).bind() },
                category = categoryId?.let { CategoryId(it) },
                time = dateTime?.atZone(zoneId)?.toInstant() ?: Instant.now(),
                settled = true,
                metadata = metadata,
                lastUpdated = dateTime?.atZone(zoneId)?.toInstant() ?: Instant.now(),
                removed = isDeleted,
                value = value
            )
        }
    }

    private fun TransactionEntity.toExpenseModel(accountAssetCode: AssetCode?): Either<String, Expense> {
        return either {
            val zoneId = ZoneId.systemDefault()
            val metadata = TransactionMetadata(
                recurringRuleId = recurringRuleId,
                loanId = loanId,
                loanRecordId = loanRecordId
            )

            val value = Value(
                amount = PositiveDouble.from(amount).bind(),
                asset = accountAssetCode ?: AssetCode.from("").bind()
            )

            Expense(
                id = TransactionId(id),
                title = title?.let { NotBlankTrimmedString.from(it).bind() },
                description = description?.let { NotBlankTrimmedString.from(it).bind() },
                category = categoryId?.let { CategoryId(it) },
                time = dateTime?.atZone(zoneId)?.toInstant() ?: Instant.now(),
                settled = true,
                metadata = metadata,
                lastUpdated = dateTime?.atZone(zoneId)?.toInstant() ?: Instant.now(),
                removed = isDeleted,
                value = value
            )
        }
    }

    private fun TransactionEntity.toTransferModel(
        fromAccountAssetCode: AssetCode?,
        toAccountAssetCode: AssetCode?
    ): Either<String, Transfer> {
        return either {
            val zoneId = ZoneId.systemDefault()
            val metadata = TransactionMetadata(
                recurringRuleId = recurringRuleId,
                loanId = loanId,
                loanRecordId = loanRecordId
            )

            val fromValue = Value(
                amount = PositiveDouble.from(amount).bind(),
                asset = fromAccountAssetCode ?: AssetCode.from("").bind()
            )

            val toValue = Value(
                amount = toAmount?.let { PositiveDouble.from(it).bind() }
                    ?: raise("toAmount cannot be null for transfers"),
                asset = toAccountAssetCode ?: AssetCode.from("").bind()
            )

            Transfer(
                id = TransactionId(id),
                title = title?.let { NotBlankTrimmedString.from(it).bind() },
                description = description?.let { NotBlankTrimmedString.from(it).bind() },
                category = categoryId?.let { CategoryId(it) },
                time = dateTime?.atZone(zoneId)?.toInstant() ?: Instant.now(),
                settled = true,
                metadata = metadata,
                lastUpdated = dateTime?.atZone(zoneId)?.toInstant() ?: Instant.now(),
                removed = isDeleted,
                fromAccount = AccountId(accountId),
                fromValue = fromValue,
                toAccount = AccountId(toAccountId!!),
                toValue = toValue
            )
        }
    }
}