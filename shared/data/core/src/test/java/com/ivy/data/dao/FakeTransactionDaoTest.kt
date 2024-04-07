package com.ivy.data.dao

import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.fake.FakeTransactionDao
import com.ivy.data.db.entity.TransactionEntity
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.UUID

@Suppress("LargeClass")
class FakeTransactionDaoTest {

    private lateinit var transactionDao: FakeTransactionDao

    @Before
    fun setup() {
        transactionDao = FakeTransactionDao()
    }

    @Test
    fun `find all - empty transactions`() = runTest {
        // when
        val res = transactionDao.findAll()

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all limit 1 - empty transactions`() = runTest {
        // when
        val res = transactionDao.findAll_LIMIT_1()

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all limit 1 - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all transfer to account - empty transactions`() = runTest {
        // given
        val toAccountId = UUID.randomUUID()

        // when
        val res =
            transactionDao.findAllTransfersToAccount(toAccountId)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all transfer to account - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all transfer to account and between - empty transactions`() = runTest {
        // given
        val toAccountId = UUID.randomUUID()
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()

        // when
        val res = transactionDao.findAllTransfersToAccountBetween(toAccountId, startDate, endDate)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all transfer to account and between - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all between - empty transactions`() = runTest {
        // given
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()

        // when
        val res = transactionDao.findAllBetween(startDate, endDate)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all between - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all by account and between - empty transactions`() = runTest {
        // given
        val accountId = UUID.randomUUID()
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()

        // when
        val res = transactionDao.findAllByAccountAndBetween(accountId, startDate, endDate)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all by account and between - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all by category and between - empty transactions`() = runTest {
        // given
        val categoryId = UUID.randomUUID()
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()

        // when
        val res = transactionDao.findAllByCategoryAndBetween(categoryId, startDate, endDate)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all by category and between - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all unspecified and between - empty transactions`() = runTest {
        // given
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()

        // when
        val res = transactionDao.findAllUnspecifiedAndBetween(startDate, endDate)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all unspecified and between - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all to account and between - empty transactions`() = runTest {
        // given
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()
        val toAccountId = UUID.randomUUID()

        // when
        val res = transactionDao.findAllToAccountAndBetween(toAccountId, startDate, endDate)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all to account and between - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all by recurring rule id - empty transactions`() = runTest {
        // given
        val recurringRuleId = UUID.randomUUID()

        // when
        val res = transactionDao.findAllByRecurringRuleId(recurringRuleId)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all by recurring rule id - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all between and by recurring rule id - empty transactions`() = runTest {
        // given
        val startDate = LocalDateTime.now().minusDays(7)
        val endDate = LocalDateTime.now()
        val recurringRuleId = UUID.randomUUID()

        // when
        val res =
            transactionDao.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)

        // then
        res shouldBe emptyList()
    }

    @Test
    fun `find all between and by recurring rule id - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find by id - empty transactions`() = runTest {
        // given
        val transactionId = UUID.randomUUID()

        // when
        val res = transactionDao.findById(transactionId)

        // then
        res shouldBe null
    }

    @Test
    fun `find by id - existing id`() = runTest {
        // given
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

    @Test
    fun `find by id - non existing id`() = runTest {
        // given
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

    @Test
    fun `count happened transactions - empty transactions`() = runTest {
        // when
        val res = transactionDao.countHappenedTransactions()

        // then
        res shouldBe 0
    }

    @Test
    fun `count happened transactions - non empty transactions`() = runTest {
        // given
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

    @Test
    fun `find all by title matching pattern - empty transactions`() = runTest {
        // given
        val pattern = "Transaction"

        // when
        val res = transactionDao.findAllByTitleMatchingPattern(pattern)

        // then
        res shouldBe emptyList()
    }
}