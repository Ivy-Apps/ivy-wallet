package com.ivy.data.repository.mapper

import com.ivy.base.model.TransactionType
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.TransactionId
import com.ivy.data.model.TransactionMetadata
import com.ivy.data.model.Transfer
import com.ivy.data.model.common.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class TransactionMapperTest : FreeSpec({
    "map transaction to entity" - {
        val mapper = TransactionMapper()
        "income to entity" {
            // given
            val accountId = AccountId(UUID.randomUUID())
            val instant = Instant.EPOCH
            val categoryId = CategoryId(UUID.randomUUID())
            val recurringRuleId = UUID.randomUUID()
            val loanId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()
            val transactionId = TransactionId(UUID.randomUUID())

            val income = Income(
                id = transactionId,
                title = NotBlankTrimmedString("Income"),
                description = NotBlankTrimmedString("Income desc"),
                category = categoryId,
                time = instant,
                settled = true,
                metadata = TransactionMetadata(
                    recurringRuleId = recurringRuleId,
                    loanId = loanId,
                    loanRecordId = loanRecordId
                ),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(amount = PositiveDouble(100.0), asset = AssetCode("NGN")),
                account = accountId
            )

            // when
            val entity = with(mapper) { income.toEntity(accountId) }

            // then
            entity shouldBe TransactionEntity(
                accountId = accountId.value,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                toAmount = 100.0,
                title = "Income",
                description = "Income desc",
                dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime(),
                categoryId = categoryId.value,
                dueDate = null,
                recurringRuleId = recurringRuleId,
                attachmentUrl = null,
                loanId = loanId,
                loanRecordId = loanRecordId,
                isSynced = false,
                isDeleted = false,
                id = transactionId.value
            )
        }

        "expense to entity" {
            // given
            val accountId = AccountId(UUID.randomUUID())
            val instant = Instant.EPOCH
            val categoryId = CategoryId(UUID.randomUUID())
            val recurringRuleId = UUID.randomUUID()
            val loanId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()
            val transactionId = TransactionId(UUID.randomUUID())

            val expense = Expense(
                id = transactionId,
                title = NotBlankTrimmedString("Expense"),
                description = NotBlankTrimmedString("Expense desc"),
                category = categoryId,
                time = instant,
                settled = true,
                metadata = TransactionMetadata(
                    recurringRuleId = recurringRuleId,
                    loanId = loanId,
                    loanRecordId = loanRecordId
                ),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(amount = PositiveDouble(100.0), asset = AssetCode("NGN")),
                account = accountId
            )

            // when
            val entity = with(mapper) { expense.toEntity(accountId) }

            // then
            entity shouldBe TransactionEntity(
                accountId = accountId.value,
                type = TransactionType.EXPENSE,
                amount = 100.0,
                toAccountId = null,
                toAmount = 100.0,
                title = "Expense",
                description = "Expense desc",
                dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime(),
                categoryId = categoryId.value,
                dueDate = null,
                recurringRuleId = recurringRuleId,
                attachmentUrl = null,
                loanId = loanId,
                loanRecordId = loanRecordId,
                isSynced = false,
                isDeleted = false,
                id = transactionId.value
            )
        }

        "transfer to entity" {
            // given
            val accountId = AccountId(UUID.randomUUID())
            val instant = Instant.EPOCH
            val categoryId = CategoryId(UUID.randomUUID())
            val recurringRuleId = UUID.randomUUID()
            val loanId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()
            val transactionId = TransactionId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val transfer = Transfer(
                id = transactionId,
                title = NotBlankTrimmedString("Transfer"),
                description = NotBlankTrimmedString("Transfer desc"),
                category = categoryId,
                time = instant,
                settled = true,
                metadata = TransactionMetadata(
                    recurringRuleId = recurringRuleId,
                    loanId = loanId,
                    loanRecordId = loanRecordId
                ),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromValue = Value(amount = PositiveDouble(100.0), asset = AssetCode("NGN")),
                fromAccount = accountId,
                toValue = Value(amount = PositiveDouble(100.0), asset = AssetCode("NGN")),
                toAccount = toAccountId
            )

            // when
            val entity = with(mapper) { transfer.toEntity(accountId) }

            // then
            entity shouldBe TransactionEntity(
                accountId = accountId.value,
                type = TransactionType.TRANSFER,
                amount = 100.0,
                toAccountId = toAccountId.value,
                toAmount = 100.0,
                title = "Transfer",
                description = "Transfer desc",
                dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime(),
                categoryId = categoryId.value,
                dueDate = null,
                recurringRuleId = recurringRuleId,
                attachmentUrl = null,
                loanId = loanId,
                loanRecordId = loanRecordId,
                isSynced = false,
                isDeleted = false,
                id = transactionId.value
            )
        }
    }

    "map entity to transaction" - {
        val mapper = TransactionMapper()
        "entity to income" - {
            // given
            val accountId = UUID.randomUUID()
            val dateTime = LocalDateTime.now()
            val categoryId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val loanId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()
            val transactionId = UUID.randomUUID()
            val assetCode = AssetCode("NGN")

            val entity = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                toAmount = null,
                title = "Income",
                description = "Income desc",
                dateTime = dateTime,
                categoryId = categoryId,
                dueDate = null,
                recurringRuleId = recurringRuleId,
                attachmentUrl = null,
                loanId = loanId,
                loanRecordId = loanRecordId,
                isSynced = true,
                isDeleted = false,
                id = transactionId
            )

            "valid income" {
                // when
                val income = with(mapper) { entity.toDomain(assetCode) }

                // then
                income.shouldBeRight() shouldBe Income(
                    id = TransactionId(transactionId),
                    title = NotBlankTrimmedString("Income"),
                    description = NotBlankTrimmedString("Income desc"),
                    category = CategoryId(categoryId),
                    time = dateTime.atZone(ZoneId.systemDefault()).toInstant(),
                    settled = true,
                    metadata = TransactionMetadata(
                        recurringRuleId, loanId, loanRecordId
                    ),
                    lastUpdated = dateTime.atZone(ZoneId.systemDefault()).toInstant(),
                    removed = false,
                    value = Value(amount = PositiveDouble(100.0), asset = assetCode),
                    account = AccountId(accountId)
                )
            }

            "title not null but blank" {
                val corruptedEntity = entity.copy(title = "")

                // when
                val income = with(mapper) { corruptedEntity.toDomain(assetCode) }

                // then
                income.shouldBeLeft()
            }

            "description not null but blank" {
                val corruptedEntity = entity.copy(description = "")

                // when
                val income = with(mapper) { corruptedEntity.toDomain(assetCode) }

                // then
                income.shouldBeLeft()
            }

            "no category is okay" {
                val noCategoryEntity = entity.copy(categoryId = null)

                // when
                val income = with(mapper) { noCategoryEntity.toDomain(assetCode) }

                // then
                income.shouldBeRight()
            }

            "no recurringId is okay" {
                val noRecurringId = entity.copy(recurringRuleId = null)

                // when
                val income = with(mapper) { noRecurringId.toDomain(assetCode) }

                // then
                income.shouldBeRight()
            }

            "no loanId is okay" {
                val noLoanId = entity.copy(loanId = null)

                // when
                val income = with(mapper) { noLoanId.toDomain(assetCode) }

                // then
                income.shouldBeRight()
            }

            "no loanRecordId is okay" {
                val noLoanRecordId = entity.copy(loanRecordId = null)

                // when
                val income = with(mapper) { noLoanRecordId.toDomain(assetCode) }

                // then
                income.shouldBeRight()
            }
        }

        "entity to expense" - {
            // given
            val accountId = UUID.randomUUID()
            val dateTime = LocalDateTime.now()
            val categoryId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val loanId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()
            val transactionId = UUID.randomUUID()
            val assetCode = AssetCode("NGN")

            val entity = TransactionEntity(
                accountId = accountId,
                type = TransactionType.EXPENSE,
                amount = 100.0,
                toAccountId = null,
                toAmount = null,
                title = "Expense",
                description = "Expense desc",
                dateTime = dateTime,
                categoryId = categoryId,
                dueDate = null,
                recurringRuleId = recurringRuleId,
                attachmentUrl = null,
                loanId = loanId,
                loanRecordId = loanRecordId,
                isSynced = true,
                isDeleted = false,
                id = transactionId
            )

            "valid expense" {
                // when
                val expense = with(mapper) { entity.toDomain(assetCode) }

                // then
                expense.shouldBeRight() shouldBe Expense(
                    id = TransactionId(transactionId),
                    title = NotBlankTrimmedString("Expense"),
                    description = NotBlankTrimmedString("Expense desc"),
                    category = CategoryId(categoryId),
                    time = dateTime.atZone(ZoneId.systemDefault()).toInstant(),
                    settled = true,
                    metadata = TransactionMetadata(
                        recurringRuleId, loanId, loanRecordId
                    ),
                    lastUpdated = dateTime.atZone(ZoneId.systemDefault()).toInstant(),
                    removed = false,
                    value = Value(amount = PositiveDouble(100.0), asset = assetCode),
                    account = AccountId(accountId)
                )
            }

            "title not null but blank" {
                val corruptedEntity = entity.copy(title = "")

                // when
                val expense = with(mapper) { corruptedEntity.toDomain(assetCode) }

                // then
                expense.shouldBeLeft()
            }

            "description not null but blank" {
                val corruptedEntity = entity.copy(description = "")

                // when
                val expense = with(mapper) { corruptedEntity.toDomain(assetCode) }

                // then
                expense.shouldBeLeft()
            }

            "no category is okay" {
                val noCategoryEntity = entity.copy(categoryId = null)

                // when
                val expense = with(mapper) { noCategoryEntity.toDomain(assetCode) }

                // then
                expense.shouldBeRight()
            }

            "no recurringId is okay" {
                val noRecurringId = entity.copy(recurringRuleId = null)

                // when
                val expense = with(mapper) { noRecurringId.toDomain(assetCode) }

                // then
                expense.shouldBeRight()
            }

            "no loanId is okay" {
                val noLoanId = entity.copy(loanId = null)

                // when
                val expense = with(mapper) { noLoanId.toDomain(assetCode) }

                // then
                expense.shouldBeRight()
            }

            "no loanRecordId is okay" {
                val noLoanRecordId = entity.copy(loanRecordId = null)

                // when
                val expense = with(mapper) { noLoanRecordId.toDomain(assetCode) }

                // then
                expense.shouldBeRight()
            }
        }

        "entity to transfer" - {
            // given
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val dateTime = LocalDateTime.now()
            val categoryId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val loanId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()
            val transactionId = UUID.randomUUID()
            val assetCode = AssetCode("NGN")

            val entity = TransactionEntity(
                accountId = accountId,
                type = TransactionType.TRANSFER,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                title = "Transfer",
                description = "Transfer desc",
                dateTime = dateTime,
                categoryId = categoryId,
                dueDate = null,
                recurringRuleId = recurringRuleId,
                attachmentUrl = null,
                loanId = loanId,
                loanRecordId = loanRecordId,
                isSynced = false,
                isDeleted = false,
                id = transactionId
            )

            "valid transfer" {
                // when
                val transfer = with(mapper) { entity.toDomain(assetCode, assetCode) }

                // then
                transfer.shouldBeRight() shouldBe Transfer(
                    id = TransactionId(transactionId),
                    title = NotBlankTrimmedString("Transfer"),
                    description = NotBlankTrimmedString("Transfer desc"),
                    category = CategoryId(categoryId),
                    time = dateTime.atZone(ZoneId.systemDefault()).toInstant(),
                    settled = true,
                    metadata = TransactionMetadata(
                        recurringRuleId, loanId, loanRecordId
                    ),
                    lastUpdated = dateTime.atZone(ZoneId.systemDefault()).toInstant(),
                    removed = false,
                    fromValue = Value(amount = PositiveDouble(100.0), asset = assetCode),
                    fromAccount = AccountId(accountId),
                    toValue = Value(amount = PositiveDouble(100.0), asset = assetCode),
                    toAccount = AccountId(toAccountId)
                )
            }

            "title not null but blank" {
                val corruptedEntity = entity.copy(title = "")

                // when
                val transfer = with(mapper) { corruptedEntity.toDomain(assetCode) }

                // then
                transfer.shouldBeLeft()
            }

            "description not null but blank" {
                val corruptedEntity = entity.copy(description = "")

                // when
                val transfer = with(mapper) { corruptedEntity.toDomain(assetCode) }

                // then
                transfer.shouldBeLeft()
            }

            "no category is okay" {
                val noCategoryEntity = entity.copy(categoryId = null)

                // when
                val transfer = with(mapper) { noCategoryEntity.toDomain(assetCode, assetCode) }

                // then
                transfer.shouldBeRight()
            }

            "no recurringId is okay" {
                val noRecurringId = entity.copy(recurringRuleId = null)

                // when
                val transfer = with(mapper) { noRecurringId.toDomain(assetCode, assetCode) }

                // then
                transfer.shouldBeRight()
            }

            "no loanId is okay" {
                val noLoanId = entity.copy(loanId = null)

                // when
                val transfer = with(mapper) { noLoanId.toDomain(assetCode, assetCode) }

                // then
                transfer.shouldBeRight()
            }

            "no loanRecordId is okay" {
                val noLoanRecordId = entity.copy(loanRecordId = null)

                // when
                val transfer = with(mapper) { noLoanRecordId.toDomain(assetCode, assetCode) }

                // then
                transfer.shouldBeRight()
            }
        }
    }
})