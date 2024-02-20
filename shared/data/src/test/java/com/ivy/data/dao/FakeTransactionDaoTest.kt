package com.ivy.data.dao

import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.fake.FakeTransactionDao
import com.ivy.data.db.entity.TransactionEntity
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.util.UUID

@Suppress("LargeClass")
class FakeTransactionDaoTest : FreeSpec({
    fun newTransactionDao() = FakeTransactionDao()

    "find all" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()

            // when
            val res = transactionDao.findAll()

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )
            val res = transactionDao.findAll()

            // then
            res shouldBe listOf(transfer1, expense1, income1)
        }
    }

    "find all limit 1" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()

            // when
            val res = transactionDao.findAll_LIMIT_1()

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )
            val res = transactionDao.findAll_LIMIT_1()

            // then
            res shouldBe listOf(transfer1)
        }
    }

    "find all transfer to account" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val toAccountId = UUID.randomUUID()

            // when
            val res =
                transactionDao.findAllTransfersToAccount(toAccountId)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = UUID.randomUUID(),
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1, transfer2))
            val res = transactionDao.findAllTransfersToAccount(toAccountId)

            // then
            res shouldBe listOf(transfer1)
        }
    }

    "find all transfer to account and between" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val toAccountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            // when
            val res =
                transactionDao.findAllTransfersToAccountBetween(toAccountId, startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = UUID.randomUUID(),
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1, transfer2))

            val res =
                transactionDao.findAllTransfersToAccountBetween(toAccountId, startDate, endDate)

            // then
            res shouldBe listOf(transfer1)
        }
    }

    "find all between" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            // when
            val res = transactionDao.findAllBetween(startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate.plusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )

            val res = transactionDao.findAllBetween(startDate, endDate)

            // then
            res shouldBe listOf(expense1, income1, transfer1)
        }
    }

    "find all by account and between" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            // when
            val res = transactionDao.findAllByAccountAndBetween(accountId, startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate.plusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )

            val res = transactionDao.findAllByAccountAndBetween(accountId, startDate, endDate)

            // then
            res shouldBe listOf(expense1, income1, transfer1)
        }
    }

    "find all by category and between" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val categoryId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            // when
            val res = transactionDao.findAllByCategoryAndBetween(categoryId, startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val categoryId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = startDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = endDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate.plusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = UUID.randomUUID(),
                dateTime = startDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )
            val res = transactionDao.findAllByCategoryAndBetween(categoryId, startDate, endDate)

            // then
            res shouldBe listOf(expense1, income1, transfer1)
        }
    }

    "find all unspecified and between" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()

            // when
            val res = transactionDao.findAllUnspecifiedAndBetween(startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = UUID.randomUUID(),
                dateTime = startDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate.plusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = UUID.randomUUID(),
                dateTime = startDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )

            val res = transactionDao.findAllUnspecifiedAndBetween(startDate, endDate)

            // then
            res shouldBe listOf(expense1, income1, transfer1)
        }
    }

    "find all to account and between" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val toAccountId = UUID.randomUUID()

            // when
            val res = transactionDao.findAllToAccountAndBetween(toAccountId, startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer3 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = UUID.randomUUID(),
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = UUID.randomUUID(),
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1, transfer2, transfer3))
            val res = transactionDao.findAllToAccountAndBetween(toAccountId, startDate, endDate)

            // then
            res shouldBe listOf(transfer1)
        }
    }

    "find all by recurring rule id" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val recurringRuleId = UUID.randomUUID()

            // when
            val res = transactionDao.findAllByRecurringRuleId(recurringRuleId)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = recurringRuleId,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = recurringRuleId,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1))
            val res = transactionDao.findAllByRecurringRuleId(recurringRuleId)

            // then
            res shouldBe listOf(expense1, income1)
        }
    }

    "find all between and by recurring rule id" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val recurringRuleId = UUID.randomUUID()

            // when
            val res =
                transactionDao.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = recurringRuleId,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = recurringRuleId,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = endDate.plusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = recurringRuleId,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate,
                loanId = null,
                loanRecordId = null,
                recurringRuleId = recurringRuleId,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = startDate.minusDays(1),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )

            val res =
                transactionDao.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)

            // then
            res shouldBe listOf(expense1, income1, transfer1)
        }
    }

    "find by id" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val transactionId = UUID.randomUUID()

            // when
            val res = transactionDao.findById(transactionId)

            // then
            res shouldBe null
        }

        "existing id" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val transactionId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = transactionId,
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1))
            val res = transactionDao.findById(transactionId)

            // then
            res shouldBe income1
        }

        "non existing id" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1))
            val res = transactionDao.findById(UUID.randomUUID())

            // then
            res shouldBe null
        }
    }

    "count happened transactions" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()

            // when
            val res = transactionDao.countHappenedTransactions()

            // then
            res shouldBe 0
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )

            val res = transactionDao.countHappenedTransactions()

            // then
            res shouldBe 3
        }
    }

    "find all by title matching pattern" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val pattern = "Transaction"

            // when
            val res = transactionDao.findAllByTitleMatchingPattern(pattern)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val pattern = "Transaction"

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "A new thing",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Bought something",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )

            val res = transactionDao.findAllByTitleMatchingPattern(pattern)

            // then
            res shouldBe listOf(transfer1, expense1, income1)
        }
    }

    "count by title matching pattern" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val pattern = "Transaction"

            // when
            val res = transactionDao.countByTitleMatchingPattern(pattern)

            // then
            res shouldBe 0
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val pattern = "Transaction"

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "A new thing",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Bought something",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )

            val res = transactionDao.countByTitleMatchingPattern(pattern)

            // then
            res shouldBe 3
        }
    }

    "count by title matching pattern and category id" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val pattern = "Transaction"
            val categoryId = UUID.randomUUID()

            // when
            val res = transactionDao.countByTitleMatchingPatternAndCategoryId(pattern, categoryId)

            // then
            res shouldBe 0
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val pattern = "Transaction"
            val categoryId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "A new thing",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Bought something",
                description = "Desc",
                categoryId = UUID.randomUUID(),
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )

            val res = transactionDao.countByTitleMatchingPatternAndCategoryId(pattern, categoryId)

            // then
            res shouldBe 3
        }
    }

    "count by title matching pattern and account id" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val pattern = "Transaction"
            val accountId = UUID.randomUUID()

            // when
            val res = transactionDao.countByTitleMatchingPatternAndAccountId(pattern, accountId)

            // then
            res shouldBe 0
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val pattern = "Transaction"

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "A new thing",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = UUID.randomUUID(),
                title = "Transaction 1",
                description = "Desc",
                categoryId = UUID.randomUUID(),
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )
            val res = transactionDao.countByTitleMatchingPatternAndAccountId(pattern, accountId)

            // then
            res shouldBe 4
        }
    }

    "find all by category" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val categoryId = UUID.randomUUID()

            // when
            val res = transactionDao.findAllByCategory(categoryId)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )

            val res = transactionDao.findAllByCategory(categoryId)

            // then
            res shouldBe listOf(transfer1, expense1, income1)
        }
    }

    "find all by account" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()

            // when
            val res = transactionDao.findAllByAccount(accountId)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val income2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val expense2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = categoryId,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            val transfer2 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = true,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(
                listOf(
                    income1,
                    income2,
                    expense1,
                    expense2,
                    transfer1,
                    transfer2
                )
            )

            val res = transactionDao.findAllByAccount(accountId)

            // then
            res shouldBe listOf(transfer1, expense1, income1)
        }
    }

    "find loan transaction" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val loanId = UUID.randomUUID()

            // when
            val res = transactionDao.findLoanTransaction(loanId)

            // then
            res shouldBe null
        }

        "existing id" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val loanId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = loanId,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = UUID.randomUUID(),
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1))
            val res = transactionDao.findLoanTransaction(loanId)

            // then
            res shouldBe income1
        }

        "non existing id" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val loanId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = loanId,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = UUID.randomUUID(),
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1))
            val res = transactionDao.findLoanTransaction(UUID.randomUUID())

            // then
            res shouldBe null
        }
    }

    "find loan record transaction" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val loanRecordId = UUID.randomUUID()

            // when
            val res = transactionDao.findLoanRecordTransaction(loanRecordId)

            // then
            res shouldBe null
        }

        "existing id" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = loanRecordId,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = UUID.randomUUID(),
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1))
            val res = transactionDao.findLoanRecordTransaction(loanRecordId)

            // then
            res shouldBe income1
        }

        "non existing id" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = loanRecordId,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = UUID.randomUUID(),
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1))
            val res = transactionDao.findLoanRecordTransaction(UUID.randomUUID())

            // then
            res shouldBe null
        }
    }

    "find all by loan id" - {
        "empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val loanId = UUID.randomUUID()

            // when
            val res = transactionDao.findAllByLoanId(loanId)

            // then
            res shouldBe emptyList()
        }

        "non empty transactions" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val loanId = UUID.randomUUID()

            val income1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = loanId,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            val expense1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = loanId,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )

            val transfer1 = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                toAccountId = toAccountId,
                toAmount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.saveMany(listOf(income1, expense1, transfer1))
            val res = transactionDao.findAllByLoanId(loanId)

            // then
            res shouldBe listOf(expense1, income1)
        }
    }

    "save" - {
        "create new" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val transaction = TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.TRANSFER
            )

            // when
            transactionDao.save(transaction)
            val res = transactionDao.findById(transaction.id)

            // then
            res shouldBe transaction
        }

        "update existing" {
            // given
            val transactionDao = newTransactionDao()
            val accountId = UUID.randomUUID()
            val transactionId = UUID.randomUUID()

            val transaction = TransactionEntity(
                id = transactionId,
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            )

            // when
            transactionDao.save(transaction)
            transactionDao.save(transaction.copy(title = "New Transaction"))
            val res = transactionDao.findById(transaction.id)

            // then
            res shouldBe transaction.copy(title = "New Transaction")
        }
    }

    "save many" {
        // given
        val transactionDao = newTransactionDao()
        val accountId = UUID.randomUUID()
        val transactions = listOf(
            TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            ),
            TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )
        )

        // when
        transactionDao.saveMany(transactions)
        val res = transactionDao.findAll()

        // then
        res shouldBe transactions.sortedByDescending { it.dateTime }
    }

    "flag deleted" {
        // given
        val transactionDao = newTransactionDao()
        val accountId = UUID.randomUUID()
        val transaction = TransactionEntity(
            id = UUID.randomUUID(),
            accountId = accountId,
            title = "Transaction 1",
            description = "Desc",
            categoryId = null,
            dateTime = LocalDateTime.now(),
            loanId = null,
            loanRecordId = null,
            recurringRuleId = null,
            isDeleted = false,
            amount = 100.0,
            type = TransactionType.INCOME
        )

        // when
        transactionDao.save(transaction)
        transactionDao.flagDeleted(transaction.id)
        val res = transactionDao.findById(transaction.id)

        // then
        res shouldBe transaction.copy(isDeleted = true)
    }

    "flag deleted by recurring rule id and no date" {
        // given
        val transactionDao = newTransactionDao()
        val accountId = UUID.randomUUID()
        val recurringRuleId = UUID.randomUUID()
        val transaction = TransactionEntity(
            id = UUID.randomUUID(),
            accountId = accountId,
            title = "Transaction 1",
            description = "Desc",
            categoryId = null,
            dateTime = null,
            loanId = null,
            loanRecordId = null,
            recurringRuleId = recurringRuleId,
            isDeleted = false,
            amount = 100.0,
            type = TransactionType.INCOME
        )

        // when
        transactionDao.save(transaction)
        transactionDao.flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId)
        val res = transactionDao.findById(transaction.id)

        // then
        res shouldBe transaction.copy(isDeleted = true)
    }

    "flag deleted by account" {
        // given
        val transactionDao = newTransactionDao()
        val accountId = UUID.randomUUID()
        val transactions = listOf(
            TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            ),
            TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )
        )

        // when
        transactionDao.saveMany(transactions)
        transactionDao.flagDeletedByAccountId(accountId)
        val res = transactionDao.findByIsSyncedAndIsDeleted(synced = false, deleted = true)

        // then
        res shouldBe transactions.map { it.copy(isDeleted = true) }
            .sortedByDescending { it.dateTime }
    }

    "delete by id" {
        // given
        val transactionDao = newTransactionDao()
        val accountId = UUID.randomUUID()
        val transaction = TransactionEntity(
            id = UUID.randomUUID(),
            accountId = accountId,
            title = "Transaction 1",
            description = "Desc",
            categoryId = null,
            dateTime = LocalDateTime.now(),
            loanId = null,
            loanRecordId = null,
            recurringRuleId = null,
            isDeleted = false,
            amount = 100.0,
            type = TransactionType.INCOME
        )

        // when
        transactionDao.save(transaction)
        transactionDao.deleteById(transaction.id)
        val res = transactionDao.findById(transaction.id)

        // then
        res shouldBe null
    }

    "delete by account id" {
        // given
        val transactionDao = newTransactionDao()
        val accountId = UUID.randomUUID()
        val transactions = listOf(
            TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.INCOME
            ),
            TransactionEntity(
                id = UUID.randomUUID(),
                accountId = accountId,
                title = "Transaction 1",
                description = "Desc",
                categoryId = null,
                dateTime = LocalDateTime.now(),
                loanId = null,
                loanRecordId = null,
                recurringRuleId = null,
                isDeleted = false,
                amount = 100.0,
                type = TransactionType.EXPENSE
            )
        )

        // when
        transactionDao.saveMany(transactions)
        transactionDao.deleteAllByAccountId(accountId)
        val res = transactionDao.findAllByAccount(accountId)

        // then
        res shouldBe emptyList()
    }

    "delete all" {
        // given
        val transactionDao = newTransactionDao()
        val accountId1 = UUID.randomUUID()
        val accountId2 = UUID.randomUUID()
        val transaction1 = TransactionEntity(
            id = UUID.randomUUID(),
            accountId = accountId1,
            title = "Transaction 1",
            description = "Desc",
            categoryId = null,
            dateTime = LocalDateTime.now(),
            loanId = null,
            loanRecordId = null,
            recurringRuleId = null,
            isDeleted = false,
            amount = 100.0,
            type = TransactionType.INCOME
        )

        val transaction2 = TransactionEntity(
            id = UUID.randomUUID(),
            accountId = accountId2,
            title = "Transaction 1",
            description = "Desc",
            categoryId = null,
            dateTime = LocalDateTime.now(),
            loanId = null,
            loanRecordId = null,
            recurringRuleId = null,
            isDeleted = false,
            amount = 100.0,
            type = TransactionType.EXPENSE
        )

        // when
        transactionDao.saveMany(listOf(transaction1, transaction2))
        transactionDao.deleteAll()
        val res = transactionDao.findAll()

        // then
        res shouldBe emptyList()
    }
})
