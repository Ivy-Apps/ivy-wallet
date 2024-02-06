package com.ivy.data.testing

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
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class FakeTransactionRepositoryTest : FreeSpec({
    fun newRepository() = FakeTransactionRepository()

    fun toInstant(localDateTime: LocalDateTime): Instant {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant()
    }

    "find all" - {
        "empty transactions" {
            //given
            val repository = newRepository()

            //when
            val res = repository.findAll()

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )
            val res = repository.findAll()

            //then
            res shouldBe listOf(income1, expense1, transfer1)
        }
    }

    "find all limit 1" - {
        "empty transactions" {
            //given
            val repository = newRepository()

            //when
            val res = repository.findAll_LIMIT_1()

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )
            val res = repository.findAll_LIMIT_1()

            //then
            res shouldBe listOf(income1)
        }
    }

    "find all income transactions" - {
        "empty transactions" {
            //given
            val repository = newRepository()

            //when
            val res = repository.findAllIncome()

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, transfer1)
            )
            val res = repository.findAllIncome()

            //then
            res shouldBe listOf(income1)
        }
    }

    "find all expense transactions" - {
        "empty transactions" {
            //given
            val repository = newRepository()

            //when
            val res = repository.findAllExpense()

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, expense2, transfer1)
            )

            val res = repository.findAllExpense()

            //then
            res shouldBe listOf(expense1)
        }
    }

    "find all transfer transactions" - {
        "empty transactions" {
            //given
            val repository = newRepository()

            //when
            val res = repository.findAllTransfer()

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1, transfer2)
            )

            val res = repository.findAllTransfer()

            //then
            res shouldBe listOf(transfer1)
        }
    }

    "find all income by account" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()

            //when
            val res = repository.findAllIncomeByAccount(AccountId(accountId))

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            repository.saveMany(
                AccountId(UUID.randomUUID()),
                listOf(income2)
            )
            val res = repository.findAllIncomeByAccount(accountId)

            //then
            res shouldBe listOf(income1)
        }
    }

    "find all expense by account" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()

            //when
            val res = repository.findAllExpenseByAccount(AccountId(accountId))

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            repository.saveMany(
                AccountId(UUID.randomUUID()),
                listOf(expense2)
            )
            val res = repository.findAllExpenseByAccount(accountId)

            //then
            res shouldBe listOf(expense1)
        }
    }

    "find all transfer by account" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()

            //when
            val res = repository.findAllTransferByAccount(AccountId(accountId))

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            repository.saveMany(
                AccountId(UUID.randomUUID()),
                listOf(transfer2)
            )
            val res = repository.findAllTransferByAccount(accountId)

            //then
            res shouldBe listOf(transfer1)
        }
    }

    "find all income by account between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllIncomeByAccountBetween(AccountId(accountId), startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            repository.saveMany(
                AccountId(UUID.randomUUID()),
                listOf(income2)
            )
            val res = repository.findAllIncomeByAccountBetween(accountId, startDate, endDate)

            //then
            res shouldBe listOf(income1)
        }
    }

    "find all expense by account between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllExpenseByAccountBetween(AccountId(accountId), startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            repository.saveMany(
                AccountId(UUID.randomUUID()),
                listOf(expense2)
            )
            val res = repository.findAllExpenseByAccountBetween(accountId, startDate, endDate)

            //then
            res shouldBe listOf(expense1)
        }
    }

    "find all transfer by account between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllTransferByAccountBetween(AccountId(accountId), startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            repository.saveMany(
                AccountId(UUID.randomUUID()),
                listOf(transfer2)
            )
            val res = repository.findAllTransferByAccountBetween(accountId, startDate, endDate)

            //then
            res shouldBe listOf(transfer1)
        }
    }

    "find all transfer to account" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()

            //when
            val res =
                repository.findAllTransfersToAccount(AccountId(accountId))

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = AccountId(UUID.randomUUID()),
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1, transfer2)
            )

            val res = repository.findAllTransfersToAccount(toAccountId)

            //then
            res shouldBe listOf(transfer1)
        }
    }

    "find all transfer to account and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllTransfersToAccountBetween(
                    AccountId(accountId),
                    startDate,
                    endDate
                )

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = AccountId(UUID.randomUUID()),
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1, transfer2)
            )

            val res = repository.findAllTransfersToAccountBetween(toAccountId, startDate, endDate)

            //then
            res shouldBe listOf(transfer1)
        }
    }

    "find all between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res = repository.findAllBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate.plusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate.plusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )
            val res = repository.findAllBetween(startDate, endDate)

            //then
            res shouldBe listOf(expense1, income1, transfer1)
        }
    }

    "find all by account and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllByAccountAndBetween(AccountId(accountId), startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate.plusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate.plusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            repository.saveMany(AccountId(UUID.randomUUID()), listOf(income2, expense2, transfer2))
            val res = repository.findAllByAccountAndBetween(accountId, startDate, endDate)

            //then
            res shouldBe listOf(expense1, income1, transfer1)
        }
    }

    "find all by category and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val categoryId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllByCategoryAndBetween(CategoryId(categoryId), startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val categoryId = CategoryId(UUID.randomUUID())
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate.plusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate.plusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )

            val res = repository.findAllByCategoryAndBetween(categoryId, startDate, endDate)

            //then
            res shouldBe listOf(expense1, income1, transfer1)
        }
    }

    "find all unspecified and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllUnspecifiedAndBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate.plusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate.plusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 6"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )

            val res = repository.findAllUnspecifiedAndBetween(startDate, endDate)

            //then
            res shouldBe listOf(expense1, income1, transfer1)
        }
    }

    "find all income by category and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val categoryId = CategoryId(UUID.randomUUID())
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllIncomeByCategoryAndBetween(categoryId, startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val categoryId = CategoryId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income3 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, income3, expense1, transfer1)
            )

            val res = repository.findAllIncomeByCategoryAndBetween(categoryId, startDate, endDate)

            //then
            res shouldBe listOf(income1)
        }
    }

    "find all expense by category and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val categoryId = CategoryId(UUID.randomUUID())
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllExpenseByCategoryAndBetween(categoryId, startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val categoryId = CategoryId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(endDate.plusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate.plusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense3 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, expense2, expense3, transfer1)
            )

            val res = repository.findAllExpenseByCategoryAndBetween(categoryId, startDate, endDate)

            //then
            res shouldBe listOf(expense1)
        }
    }

    "find all transfer by category and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val categoryId = CategoryId(UUID.randomUUID())
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllTransferByCategoryAndBetween(categoryId, startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val categoryId = CategoryId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer3 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1, transfer2, transfer3)
            )

            val res = repository.findAllTransferByCategoryAndBetween(categoryId, startDate, endDate)

            //then
            res shouldBe listOf(transfer1)
        }
    }

    "find all unspecified income and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllUnspecifiedIncomeAndBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income3 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, income3, expense1, transfer1)
            )

            val res = repository.findAllUnspecifiedIncomeAndBetween(startDate, endDate)

            //then
            res shouldBe listOf(income1)
        }
    }

    "find all unspecified expense and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllUnspecifiedExpenseAndBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate.plusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate.plusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense3 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, expense2, expense3, transfer1)
            )

            val res = repository.findAllUnspecifiedExpenseAndBetween(startDate, endDate)

            //then
            res shouldBe listOf(expense1)
        }
    }

    "find all unspecified transfer and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllUnspecifiedTransferAndBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer3 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1, transfer2, transfer3)
            )

            val res = repository.findAllUnspecifiedTransferAndBetween(startDate, endDate)

            //then
            res shouldBe listOf(transfer1)
        }
    }

    "find all to account and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val toAccountId = AccountId(UUID.randomUUID())

            //when
            val res =
                repository.findAllToAccountAndBetween(toAccountId, startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer3 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = AccountId(UUID.randomUUID()),
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1, transfer2, transfer3)
            )

            val res = repository.findAllToAccountAndBetween(toAccountId, startDate, endDate)

            //then
            res shouldBe listOf(transfer1)
        }
    }

    "find all by recurring rule id" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val recurringRuleId = UUID.randomUUID()

            //when
            val res =
                repository.findAllByRecurringRuleId(recurringRuleId)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val recurringRuleId = UUID.randomUUID()

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            val res = repository.findAllByRecurringRuleId(recurringRuleId)

            //then
            res shouldBe listOf(income1, expense1)
        }
    }

    "find all income between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllIncomeBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val categoryId = CategoryId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income3 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, income3, expense1, transfer1)
            )

            val res = repository.findAllIncomeBetween(startDate, endDate)

            //then
            res shouldBe listOf(income1, income3)
        }
    }

    "find all expense between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res =
                repository.findAllExpenseBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val categoryId = CategoryId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(endDate.plusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate.plusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense3 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, expense2, expense3, transfer1)
            )

            val res = repository.findAllExpenseBetween(startDate, endDate)

            //then
            res shouldBe listOf(expense1, expense3)
        }
    }

    "find all transfer between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            //when
            val res = repository.findAllTransferBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val categoryId = CategoryId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer3 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1, transfer2, transfer3)
            )

            val res = repository.findAllTransferBetween(startDate, endDate)

            //then
            res shouldBe listOf(transfer1, transfer3)
        }
    }

    "find all between and by recurring rule id" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val recurringRuleId = UUID.randomUUID()

            //when
            val res =
                repository.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val recurringRuleId = UUID.randomUUID()

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate),
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, null, null),
                lastUpdated = toInstant(endDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(endDate.plusDays(1)),
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, null, null),
                lastUpdated = toInstant(endDate.plusDays(1)),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate.minusDays(1)),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(startDate.minusDays(1)),
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )

            val res =
                repository.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)

            //then
            res shouldBe listOf(expense1, income1, transfer1)
        }
    }

    "find by id" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val transactionId = TransactionId(UUID.randomUUID())

            //when
            val res = repository.findById(transactionId)

            //then
            res shouldBe null
        }

        "existing id" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val transactionId = TransactionId(UUID.randomUUID())

            val income1 = Income(
                id = transactionId,
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            val res =
                repository.findById(transactionId)

            //then
            res shouldBe income1
        }

        "non existing id" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            val res =
                repository.findById(TransactionId(UUID.randomUUID()))

            //then
            res shouldBe null
        }
    }

    "count happened transactions" - {
        "empty transactions" {
            //given
            val repository = newRepository()

            //when
            val res = repository.countHappenedTransactions()

            //then
            res shouldBe 0
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )

            val res = repository.countHappenedTransactions()

            //then
            res shouldBe 3
        }
    }

    "find all by title matching pattern" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val pattern = "Transaction"

            //when
            val res = repository.findAllByTitleMatchingPattern(pattern)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val pattern = "Transaction"

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("A new thing"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Bought something"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )

            val res = repository.findAllByTitleMatchingPattern(pattern)

            //then
            res shouldBe listOf(income1, expense1, transfer1)
        }
    }

    "count by title matching pattern" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val pattern = "Transaction"

            //when
            val res = repository.countByTitleMatchingPattern(pattern)

            //then
            res shouldBe 0
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val pattern = "Transaction"

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("A new thing"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Bought something"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )

            val res = repository.countByTitleMatchingPattern(pattern)

            //then
            res shouldBe 3
        }
    }

    "count by title matching pattern and category id" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val pattern = "Transaction"
            val categoryId = CategoryId(UUID.randomUUID())

            //when
            val res = repository.countByTitleMatchingPatternAndCategoryId(pattern, categoryId)

            //then
            res shouldBe 0
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val pattern = "Transaction"
            val categoryId = CategoryId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("A new thing"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Bought something"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )

            val res = repository.countByTitleMatchingPatternAndCategoryId(pattern, categoryId)

            //then
            res shouldBe 3
        }
    }

    "count by title matching pattern and account id" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val pattern = "Transaction"
            val accountId = AccountId(UUID.randomUUID())

            //when
            val res = repository.countByTitleMatchingPatternAndAccountId(pattern, accountId)

            //then
            res shouldBe 0
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val pattern = "Transaction"

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("A new thing"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = CategoryId(UUID.randomUUID()),
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = AccountId(UUID.randomUUID()),
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, transfer1)
            )

            repository.saveMany(
                AccountId(UUID.randomUUID()),
                listOf(expense2, transfer2)
            )

            val res = repository.countByTitleMatchingPatternAndAccountId(pattern, accountId)

            //then
            res shouldBe 3
        }
    }

    "find all by category" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val categoryId = CategoryId(UUID.randomUUID())

            //when
            val res = repository.findAllByCategory(categoryId)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val categoryId = CategoryId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            //when
            repository.saveMany(
                accountId,
                listOf(income1, income2, expense1, expense2, transfer1, transfer2)
            )

            val res =
                repository.findAllByCategory(categoryId)

            //then
            res shouldBe listOf(income1, expense1, transfer1)
        }
    }

    "find all by account" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())

            //when
            val res = repository.findAllByAccount(accountId)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val categoryId = CategoryId(UUID.randomUUID())

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val income2 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense2 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 4"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = categoryId,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer2 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = true,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )


            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, expense2, transfer1)
            )

            repository.saveMany(
                AccountId(UUID.randomUUID()),
                listOf(income2, transfer2)
            )

            val res =
                repository.findAllByAccount(accountId)

            //then
            res shouldBe listOf(income1, expense1, transfer1)
        }
    }

    "find loan transaction" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val loanId = UUID.randomUUID()

            //when
            val res = repository.findLoanTransaction(loanId)

            //then
            res shouldBe null
        }

        "existing id" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val loanId = UUID.randomUUID()

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, loanId, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, UUID.randomUUID(), null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            val res = repository.findLoanTransaction(loanId)

            //then
            res shouldBe income1
        }

        "non existing id" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val loanId = UUID.randomUUID()

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, loanId, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, UUID.randomUUID(), null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            val res = repository.findLoanTransaction(UUID.randomUUID())

            //then
            res shouldBe null
        }
    }

    "find loan record transaction" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val loanRecordId = UUID.randomUUID()

            //when
            val res = repository.findLoanRecordTransaction(loanRecordId)

            //then
            res shouldBe null
        }

        "existing id" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val loanRecordId = UUID.randomUUID()

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, loanRecordId),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, UUID.randomUUID()),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            val res = repository.findLoanRecordTransaction(loanRecordId)

            //then
            res shouldBe income1
        }

        "non existing id" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val loanRecordId = UUID.randomUUID()

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, loanRecordId),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, UUID.randomUUID()),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            val res = repository.findLoanRecordTransaction(UUID.randomUUID())

            //then
            res shouldBe null
        }
    }

    "find all by loan id" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val loanId = UUID.randomUUID()

            //when
            val res = repository.findAllByLoanId(loanId)

            //then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val toAccountId = AccountId(UUID.randomUUID())
            val loanId = UUID.randomUUID()

            val income1 = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, loanId, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val expense1 = Expense(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 3"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, loanId, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            val transfer1 = Transfer(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 5"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                fromAccount = accountId,
                fromValue = Value(PositiveDouble(100.0), AssetCode("NGN")),
                toAccount = toAccountId,
                toValue = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.saveMany(
                accountId,
                listOf(income1, expense1, transfer1)
            )

            val res = repository.findAllByLoanId(loanId)

            //then
            res shouldBe listOf(income1, expense1)
        }
    }

    "save" - {
        "create new" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val transaction = Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.save(accountId, transaction)
            val res = repository.findById(transaction.id)

            //then
            res shouldBe transaction
        }

        "update existing" {
            //given
            val repository = newRepository()
            val accountId = AccountId(UUID.randomUUID())
            val transactionId = TransactionId(UUID.randomUUID())

            val transaction = Income(
                id = transactionId,
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )

            //when
            repository.save(accountId, transaction)
            repository.save(
                accountId,
                transaction.copy(title = NotBlankTrimmedString("New Transaction"))
            )
            val res = repository.findById(transaction.id)

            //then
            res shouldBe Income(
                id = transactionId,
                title = NotBlankTrimmedString("New Transaction"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )
        }
    }

    "save many" {
        //given
        val repository = newRepository()
        val accountId = AccountId(UUID.randomUUID())
        val transactions = listOf(
            Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            ),
            Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )
        )

        //when
        repository.saveMany(accountId, transactions)
        val res = repository.findAll()

        //then
        res shouldBe transactions
    }

    "flag deleted" {
        //given
        val repository = newRepository()
        val accountId = AccountId(UUID.randomUUID())
        val transaction = Income(
            id = TransactionId(UUID.randomUUID()),
            title = NotBlankTrimmedString("Transaction 1"),
            description = NotBlankTrimmedString("Desc"),
            category = null,
            time = Instant.EPOCH,
            settled = true,
            metadata = TransactionMetadata(null, null, null),
            lastUpdated = Instant.EPOCH,
            removed = false,
            value = Value(PositiveDouble(100.0), AssetCode("NGN"))
        )


        //when
        repository.save(accountId, transaction)
        repository.flagDeleted(transaction.id)
        val res = repository.findById(transaction.id)

        //then
        res shouldBe transaction.copy(removed = true)
    }

    "flag deleted by recurring rule id and no date" {
        //given
        val repository = newRepository()
        val accountId = AccountId(UUID.randomUUID())
        val recurringRuleId = UUID.randomUUID()
        val transaction = Income(
            id = TransactionId(UUID.randomUUID()),
            title = NotBlankTrimmedString("Transaction 1"),
            description = NotBlankTrimmedString("Desc"),
            category = null,
            time = Instant.EPOCH,
            settled = true,
            metadata = TransactionMetadata(recurringRuleId, null, null),
            lastUpdated = Instant.EPOCH,
            removed = false,
            value = Value(PositiveDouble(100.0), AssetCode("NGN"))
        )


        //when
        repository.save(accountId, transaction)
        repository.flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId)
        val res = repository.findById(transaction.id)

        //then
        res shouldBe transaction.copy(removed = true)
    }

    "flag deleted by account" {
        //given
        val repository = newRepository()
        val accountId = AccountId(UUID.randomUUID())
        val transactions = listOf(
            Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            ),
            Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )
        )

        //when
        repository.saveMany(accountId, transactions)
        repository.flagDeletedByAccountId(accountId)
        val res = repository.findByIsSyncedAndIsDeleted(synced = false, deleted = true)

        //then
        res shouldBe transactions.map { it.copy(removed = true) }
    }

    "delete by id" {
        //given
        val repository = newRepository()
        val accountId = AccountId(UUID.randomUUID())
        val transaction = Income(
            id = TransactionId(UUID.randomUUID()),
            title = NotBlankTrimmedString("Transaction 1"),
            description = NotBlankTrimmedString("Desc"),
            category = null,
            time = Instant.EPOCH,
            settled = true,
            metadata = TransactionMetadata(null, null, null),
            lastUpdated = Instant.EPOCH,
            removed = false,
            value = Value(PositiveDouble(100.0), AssetCode("NGN"))
        )

        //when
        repository.save(accountId, transaction)
        repository.deleteById(transaction.id)
        val res = repository.findById(transaction.id)

        //then
        res shouldBe null
    }

    "delete by account id" {
        //given
        val repository = newRepository()
        val accountId = AccountId(UUID.randomUUID())
        val transactions = listOf(
            Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            ),
            Income(
                id = TransactionId(UUID.randomUUID()),
                title = NotBlankTrimmedString("Transaction 2"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = Instant.EPOCH,
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN"))
            )
        )

        //when
        repository.saveMany(accountId, transactions)
        repository.deleteAllByAccountId(accountId)
        val res = repository.findAllByAccount(accountId)

        //then
        res shouldBe emptyList()
    }

    "delete all" {
        //given
        val repository = newRepository()
        val accountId1 = AccountId(UUID.randomUUID())
        val accountId2 = AccountId(UUID.randomUUID())
        val transaction1 = Income(
            id = TransactionId(UUID.randomUUID()),
            title = NotBlankTrimmedString("Transaction 1"),
            description = NotBlankTrimmedString("Desc"),
            category = null,
            time = Instant.EPOCH,
            settled = true,
            metadata = TransactionMetadata(null, null, null),
            lastUpdated = Instant.EPOCH,
            removed = false,
            value = Value(PositiveDouble(100.0), AssetCode("NGN"))
        )

        val transaction2 = Income(
            id = TransactionId(UUID.randomUUID()),
            title = NotBlankTrimmedString("Transaction 2"),
            description = NotBlankTrimmedString("Desc"),
            category = null,
            time = Instant.EPOCH,
            settled = true,
            metadata = TransactionMetadata(null, null, null),
            lastUpdated = Instant.EPOCH,
            removed = false,
            value = Value(PositiveDouble(100.0), AssetCode("NGN"))
        )

        //when
        repository.save(accountId1, transaction1)
        repository.save(accountId2, transaction2)
        repository.deleteAll()
        val res = repository.findAll()

        //then
        res shouldBe emptyList()
    }
})
