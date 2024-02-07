package com.ivy.data.repository.impl

import com.ivy.base.model.TransactionType
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.model.Account
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.TransactionId
import com.ivy.data.model.TransactionMetadata
import com.ivy.data.model.Transfer
import com.ivy.data.model.common.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.data.source.LocalTransactionDataSource
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

class TransactionRepositoryImplTest : FreeSpec({
    val dataSource = mockk<LocalTransactionDataSource>()
    val accountRepo = mockk<AccountRepository>()
    val mapper = TransactionMapper()

    fun newRepository(): TransactionRepository = TransactionRepositoryImpl(
        accountRepository = accountRepo,
        mapper = mapper,
        dataSource = dataSource
    )

    fun toInstant(localDateTime: LocalDateTime): Instant {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant()
    }

    "find all" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            coEvery { dataSource.findAll() } returns emptyList()

            //when
            val res = repository.findAll()

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val validIncomeId = UUID.randomUUID()
            val invalidIncomeId = UUID.randomUUID()
            val validExpenseId = UUID.randomUUID()
            val invalidExpenseId = UUID.randomUUID()
            val validTransferId = UUID.randomUUID()
            val invalidTransferId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val transactionDateTime = LocalDateTime.now()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val toAccount = Account(
                id = AccountId(toAccountId),
                name = NotBlankTrimmedString("Cash"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = "Transaction 1",
                toAmount = 100.0,
                description = "Desc",
                dateTime = transactionDateTime,
                id = validIncomeId
            )
            val invalidIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = " ",
                toAmount = 100.0,
                description = "Desc",
                dateTime = transactionDateTime,
                id = invalidIncomeId
            )
            val validExpense = TransactionEntity(
                accountId = accountId,
                type = TransactionType.EXPENSE,
                amount = 100.0,
                toAccountId = null,
                title = "Transaction 1",
                toAmount = 100.0,
                description = "Desc",
                dateTime = transactionDateTime,
                id = validExpenseId
            )
            val invalidExpense = TransactionEntity(
                accountId = accountId,
                type = TransactionType.EXPENSE,
                amount = 100.0,
                toAccountId = null,
                title = " ",
                toAmount = 100.0,
                description = "Desc",
                dateTime = transactionDateTime,
                id = invalidExpenseId
            )
            val validTransfer = TransactionEntity(
                accountId = accountId,
                type = TransactionType.TRANSFER,
                amount = 100.0,
                toAccountId = toAccountId,
                title = "Transaction 1",
                toAmount = 100.0,
                description = "Desc",
                dateTime = transactionDateTime,
                id = validTransferId
            )
            val invalidTransfer = TransactionEntity(
                accountId = accountId,
                type = TransactionType.TRANSFER,
                amount = 100.0,
                toAccountId = toAccountId,
                title = " ",
                toAmount = 100.0,
                description = "Desc",
                dateTime = transactionDateTime,
                id = invalidTransferId
            )

            coEvery { dataSource.findAll() } returns listOf(
                validIncome,
                invalidIncome,
                validExpense,
                invalidExpense,
                validTransfer,
                invalidTransfer
            )
            coEvery { accountRepo.findById(account.id) } returns account
            coEvery { accountRepo.findById(toAccount.id) } returns toAccount

            //when
            val res = repository.findAll()

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(transactionDateTime),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(transactionDateTime),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = AccountId(accountId)
                ),
                Expense(
                    id = TransactionId(validExpenseId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(transactionDateTime),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(transactionDateTime),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = AccountId(accountId)
                ),
                Transfer(
                    id = TransactionId(validTransferId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(transactionDateTime),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(transactionDateTime),
                    removed = false,
                    fromAccount = AccountId(accountId),
                    fromValue = Value(PositiveDouble(100.0), account.asset),
                    toAccount = AccountId(toAccountId),
                    toValue = Value(PositiveDouble(100.0), toAccount.asset)
                )
            )
        }
    }

    "find all limit 1" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            coEvery { dataSource.findAll_LIMIT_1() } returns emptyList()

            //when
            val res = repository.findAll_LIMIT_1()

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val validIncomeId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val transactionDateTime = LocalDateTime.now()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = "Transaction 1",
                toAmount = 100.0,
                description = "Desc",
                dateTime = transactionDateTime,
                id = validIncomeId
            )

            coEvery { dataSource.findAll_LIMIT_1() } returns listOf(validIncome)
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findAll_LIMIT_1()

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(transactionDateTime),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(transactionDateTime),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery { dataSource.findAllBetween(startDate, endDate) } returns emptyList()

            //when
            val res = repository.findAllBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validIncomeId = UUID.randomUUID()
            val validIncome2Id = UUID.randomUUID()
            val invalidIncomeId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = "Transaction 1",
                toAmount = 100.0,
                description = "Desc",
                dateTime = startDate,
                id = validIncomeId
            )

            val validIncome2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = "Transaction 2",
                toAmount = 100.0,
                description = "Desc",
                dateTime = endDate,
                id = validIncome2Id
            )

            val invalidIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = "",
                toAmount = 100.0,
                description = "Desc",
                dateTime = endDate,
                id = invalidIncomeId
            )

            coEvery { dataSource.findAllBetween(startDate, endDate) } returns listOf(
                validIncome,
                validIncome2,
                invalidIncome
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findAllBetween(startDate, endDate)

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                ),
                Income(
                    id = TransactionId(validIncome2Id),
                    title = NotBlankTrimmedString("Transaction 2"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(endDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(endDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all by account and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                dataSource.findAllByAccountAndBetween(
                    accountId,
                    startDate,
                    endDate
                )
            } returns emptyList()

            //when
            val res =
                repository.findAllByAccountAndBetween(AccountId(accountId), startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validIncomeId = UUID.randomUUID()
            val validIncome2Id = UUID.randomUUID()
            val invalidIncomeId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = "Transaction 1",
                toAmount = 100.0,
                description = "Desc",
                dateTime = startDate,
                id = validIncomeId
            )

            val invalidIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = "",
                toAmount = 100.0,
                description = "",
                dateTime = endDate,
                id = validIncome2Id
            )

            val invalidIncome2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                toAccountId = null,
                title = "Transaction 3",
                toAmount = 100.0,
                description = "",
                dateTime = endDate,
                id = invalidIncomeId
            )

            coEvery {
                dataSource.findAllByAccountAndBetween(
                    accountId,
                    startDate,
                    endDate
                )
            } returns listOf(
                validIncome,
                invalidIncome2,
                invalidIncome
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res =
                repository.findAllByAccountAndBetween(AccountId(accountId), startDate, endDate)

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all by category and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val categoryId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                dataSource.findAllByCategoryAndBetween(
                    categoryId,
                    startDate,
                    endDate
                )
            } returns emptyList()

            //when
            val res =
                repository.findAllByCategoryAndBetween(CategoryId(categoryId), startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validIncomeId = UUID.randomUUID()
            val validIncome2Id = UUID.randomUUID()
            val invalidIncomeId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = "Transaction 1",
                toAmount = 100.0,
                description = "Desc",
                dateTime = startDate,
                id = validIncomeId,
                categoryId = categoryId
            )

            val invalidIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = "",
                toAmount = 100.0,
                description = "Desc",
                dateTime = endDate,
                id = validIncome2Id,
                categoryId = categoryId
            )

            val invalidIncome2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                toAccountId = null,
                title = "Transaction 3",
                toAmount = -100.0,
                description = "Desc",
                dateTime = endDate,
                id = invalidIncomeId,
                categoryId = categoryId
            )

            coEvery {
                dataSource.findAllByCategoryAndBetween(
                    categoryId,
                    startDate,
                    endDate
                )
            } returns listOf(
                validIncome,
                invalidIncome2,
                invalidIncome
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res =
                repository.findAllByCategoryAndBetween(CategoryId(categoryId), startDate, endDate)

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = CategoryId(categoryId),
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all unspecified and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                dataSource.findAllUnspecifiedAndBetween(
                    startDate,
                    endDate
                )
            } returns emptyList()

            //when
            val res =
                repository.findAllUnspecifiedAndBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validIncomeId = UUID.randomUUID()
            val validIncome2Id = UUID.randomUUID()
            val invalidIncomeId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                toAccountId = null,
                title = "Transaction 1",
                toAmount = 100.0,
                description = "Desc",
                dateTime = startDate,
                id = validIncomeId,
                categoryId = null
            )

            val invalidIncome = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                toAccountId = null,
                title = "Transaction 2",
                toAmount = 100.0,
                description = "Desc",
                dateTime = endDate,
                id = validIncome2Id,
                categoryId = null
            )

            val invalidIncome2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                toAccountId = null,
                title = "",
                toAmount = 100.0,
                description = "Desc",
                dateTime = endDate.plusDays(1),
                id = invalidIncomeId,
                categoryId = null
            )

            coEvery { dataSource.findAllUnspecifiedAndBetween(startDate, endDate) } returns listOf(
                validIncome,
                invalidIncome2,
                invalidIncome
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findAllUnspecifiedAndBetween(startDate, endDate)

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all to account and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val toAccountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                dataSource.findAllToAccountAndBetween(
                    toAccountId,
                    startDate,
                    endDate
                )
            } returns emptyList()

            //when
            val res =
                repository.findAllToAccountAndBetween(AccountId(toAccountId), startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val toAccount = Account(
                id = AccountId(toAccountId),
                name = NotBlankTrimmedString("Cash"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.TRANSFER,
                amount = 100.0,
                toAccountId = toAccountId,
                title = "Transaction 1",
                toAmount = 100.0,
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.TRANSFER,
                amount = 100.0,
                toAccountId = toAccountId,
                title = "Transaction 2",
                toAmount = -100.0,
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.TRANSFER,
                amount = 100.0,
                toAccountId = toAccountId,
                title = "Transaction 3",
                toAmount = 100.0,
                description = " ",
                dateTime = endDate,
                id = invalidTransactionId,
            )

            coEvery {
                dataSource.findAllToAccountAndBetween(
                    toAccountId,
                    startDate,
                    endDate
                )
            } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account
            coEvery { accountRepo.findById(toAccount.id) } returns toAccount

            //when
            val res =
                repository.findAllToAccountAndBetween(AccountId(toAccountId), startDate, endDate)

            //then
            res shouldBe listOf(
                Transfer(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    fromAccount = AccountId(accountId),
                    fromValue = Value(PositiveDouble(100.0), account.asset),
                    toAccount = AccountId(toAccountId),
                    toValue = Value(PositiveDouble(100.0), account.asset)
                )
            )
        }
    }

    "find all due to and between" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                dataSource.findAllDueToBetween(
                    startDate,
                    endDate
                )
            } returns emptyList()

            //when
            val res =
                repository.findAllDueToBetween(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val toAccount = Account(
                id = AccountId(toAccountId),
                name = NotBlankTrimmedString("Cash"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.TRANSFER,
                amount = 100.0,
                toAccountId = toAccountId,
                title = "Transaction 1",
                toAmount = 100.0,
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
                dueDate = startDate
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.TRANSFER,
                amount = -100.0,
                toAccountId = toAccountId,
                title = "Transaction 2",
                toAmount = -100.0,
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
                dueDate = startDate
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.TRANSFER,
                amount = 100.0,
                toAccountId = toAccountId,
                title = "Transaction 3",
                toAmount = 100.0,
                description = "",
                dateTime = endDate,
                id = invalidTransactionId,
                dueDate = endDate
            )

            coEvery {
                dataSource.findAllDueToBetween(
                    startDate,
                    endDate
                )
            } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account
            coEvery { accountRepo.findById(toAccount.id) } returns toAccount

            //when
            val res =
                repository.findAllDueToBetween(startDate, endDate)

            //then
            res shouldBe listOf(
                Transfer(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    fromAccount = AccountId(accountId),
                    fromValue = Value(PositiveDouble(100.0), account.asset),
                    toAccount = AccountId(toAccountId),
                    toValue = Value(PositiveDouble(100.0), account.asset)
                )
            )
        }
    }

    "find all due to between by category" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val categoryId = UUID.randomUUID()
            coEvery {
                dataSource.findAllDueToBetweenByCategory(
                    startDate,
                    endDate,
                    categoryId
                )
            } returns emptyList()

            //when
            val res =
                repository.findAllDueToBetweenByCategory(startDate, endDate, CategoryId(categoryId))

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
                dueDate = startDate,
                categoryId = categoryId
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 2",
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
                dueDate = startDate,
                categoryId = categoryId
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 3",
                description = "",
                dateTime = endDate,
                id = invalidTransactionId,
                dueDate = endDate,
                categoryId = categoryId
            )

            coEvery {
                dataSource.findAllDueToBetweenByCategory(
                    startDate,
                    endDate,
                    categoryId
                )
            } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res =
                repository.findAllDueToBetweenByCategory(startDate, endDate, CategoryId(categoryId))

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = CategoryId(categoryId),
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all due to between by unspecified category" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                dataSource.findAllDueToBetweenByCategoryUnspecified(
                    startDate,
                    endDate
                )
            } returns emptyList()

            //when
            val res =
                repository.findAllDueToBetweenByCategoryUnspecified(startDate, endDate)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
                dueDate = startDate,
                categoryId = null
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 2",
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
                dueDate = startDate,
                categoryId = null
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 3",
                description = "",
                dateTime = endDate,
                id = invalidTransactionId,
                dueDate = endDate,
                categoryId = null
            )

            coEvery {
                dataSource.findAllDueToBetweenByCategoryUnspecified(
                    startDate,
                    endDate,
                )
            } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res =
                repository.findAllDueToBetweenByCategoryUnspecified(startDate, endDate)

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all due to between by account" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = UUID.randomUUID()
            coEvery {
                dataSource.findAllDueToBetweenByAccount(
                    startDate,
                    endDate,
                    accountId
                )
            } returns emptyList()

            //when
            val res =
                repository.findAllDueToBetweenByAccount(startDate, endDate, AccountId(accountId))

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
                dueDate = startDate,
                categoryId = null
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 2",
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
                dueDate = startDate,
                categoryId = null
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 3",
                description = "",
                dateTime = endDate,
                id = invalidTransactionId,
                dueDate = endDate,
                categoryId = null
            )

            coEvery {
                dataSource.findAllDueToBetweenByAccount(
                    startDate,
                    endDate,
                    accountId
                )
            } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res =
                repository.findAllDueToBetweenByAccount(startDate, endDate, AccountId(accountId))

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all by recurring rule id" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val recurringRuleId = UUID.randomUUID()
            coEvery {
                dataSource.findAllByRecurringRuleId(recurringRuleId)
            } returns emptyList()

            //when
            val res = repository.findAllByRecurringRuleId(recurringRuleId)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
                dueDate = startDate,
                categoryId = null,
                recurringRuleId = recurringRuleId
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 2",
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
                dueDate = startDate,
                categoryId = null,
                recurringRuleId = recurringRuleId
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 3",
                description = "",
                dateTime = endDate,
                id = invalidTransactionId,
                dueDate = endDate,
                categoryId = null,
                recurringRuleId = recurringRuleId
            )

            coEvery {
                dataSource.findAllByRecurringRuleId(recurringRuleId)
            } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res =
                repository.findAllByRecurringRuleId(recurringRuleId)

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(recurringRuleId, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all between and recurring rule id" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val recurringRuleId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                dataSource.findAllBetweenAndRecurringRuleId(
                    startDate,
                    endDate,
                    recurringRuleId
                )
            } returns emptyList()

            //when
            val res =
                repository.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
                dueDate = startDate,
                categoryId = null,
                recurringRuleId = recurringRuleId
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 2",
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
                dueDate = startDate,
                categoryId = null,
                recurringRuleId = recurringRuleId
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 3",
                description = "",
                dateTime = endDate,
                id = invalidTransactionId,
                dueDate = endDate,
                categoryId = null,
                recurringRuleId = recurringRuleId
            )

            coEvery {
                dataSource.findAllBetweenAndRecurringRuleId(
                    startDate,
                    endDate,
                    recurringRuleId
                )
            } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res =
                repository.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(recurringRuleId, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find by id" - {
        "null transaction entity" {
            //given
            val repository = newRepository()
            val transactionId = UUID.randomUUID()
            coEvery { dataSource.findById(transactionId) } returns null

            //when
            val res =
                repository.findById(TransactionId(transactionId))

            //then
            res shouldBe null
        }

        "valid transaction entity" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val transaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = transactionId,
                dueDate = startDate,
                categoryId = null,
                recurringRuleId = recurringRuleId
            )

            coEvery { dataSource.findById(transactionId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findById(TransactionId(transactionId))

            //then
            res shouldBe Income(
                id = TransactionId(transactionId),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, null, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), account.asset),
                account = account.id
            )
        }

        "invalid transaction entity" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val transaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = endDate,
                id = transactionId,
                dueDate = startDate,
                categoryId = null,
            )


            coEvery { dataSource.findById(transactionId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findById(TransactionId(transactionId))

            //then
            res shouldBe null
        }
    }

    "find by is synced and is deleted" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val isSynced = true
            val isDeleted = true
            coEvery {
                dataSource.findByIsSyncedAndIsDeleted(
                    synced = isSynced,
                    deleted = isDeleted,
                )
            } returns emptyList()

            //when
            val res =
                repository.findByIsSyncedAndIsDeleted(isSynced, isDeleted)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val isSynced = true
            val isDeleted = true
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
                dueDate = startDate,
                categoryId = null,
                isSynced = isSynced,
                isDeleted = isDeleted
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 2",
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
                dueDate = startDate,
                categoryId = null,
                isSynced = isSynced,
                isDeleted = isDeleted
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 3",
                description = "",
                dateTime = endDate,
                id = invalidTransactionId,
                dueDate = endDate,
                categoryId = null,
                isSynced = isSynced,
                isDeleted = isDeleted
            )

            coEvery { dataSource.findByIsSyncedAndIsDeleted(isSynced, isDeleted) } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findByIsSyncedAndIsDeleted(isSynced, isDeleted)

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = isDeleted,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all by category" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val categoryId = UUID.randomUUID()
            coEvery { dataSource.findAllByCategory(categoryId) } returns emptyList()

            //when
            val res = repository.findAllByCategory(CategoryId(categoryId))

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
                dueDate = startDate,
                categoryId = categoryId,
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 2",
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
                dueDate = startDate,
                categoryId = categoryId,
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 3",
                description = "",
                dateTime = endDate,
                id = invalidTransactionId,
                dueDate = endDate,
                categoryId = categoryId,
            )

            coEvery { dataSource.findAllByCategory(categoryId) } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findAllByCategory(CategoryId(categoryId))

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = CategoryId(categoryId),
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find all by account" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val accountId = UUID.randomUUID()
            coEvery { dataSource.findAllByAccount(accountId) } returns emptyList()

            //when
            val res = repository.findAllByAccount(AccountId(accountId))

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
                dueDate = startDate,
                categoryId = categoryId,
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 2",
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
                dueDate = startDate,
                categoryId = categoryId,
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 3",
                description = "",
                dateTime = endDate,
                id = invalidTransactionId,
                dueDate = endDate,
                categoryId = categoryId,
            )

            coEvery { dataSource.findAllByAccount(accountId) } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findAllByAccount(AccountId(accountId))

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = CategoryId(categoryId),
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "find by loan id" - {
        "null transaction entity" {
            //given
            val repository = newRepository()
            val loanId = UUID.randomUUID()
            coEvery { dataSource.findLoanTransaction(loanId) } returns null

            //when
            val res = repository.findLoanTransaction(loanId)

            //then
            res shouldBe null
        }

        "valid transaction entity" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val loanId = UUID.randomUUID()

            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val transaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = transactionId,
                dueDate = startDate,
                categoryId = null,
                recurringRuleId = recurringRuleId,
                loanId = loanId
            )

            coEvery { dataSource.findLoanTransaction(loanId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findLoanTransaction(loanId)

            //then
            res shouldBe Income(
                id = TransactionId(transactionId),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, loanId, null),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), account.asset),
                account = account.id
            )
        }

        "invalid transaction entity" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val loanId = UUID.randomUUID()

            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val transaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = endDate,
                id = transactionId,
                dueDate = startDate,
                categoryId = null,
                loanId = loanId
            )


            coEvery { dataSource.findLoanTransaction(loanId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findLoanTransaction(loanId)

            //then
            res shouldBe null
        }
    }

    "find by loan record id" - {
        "null transaction entity" {
            //given
            val repository = newRepository()
            val loanRecordId = UUID.randomUUID()
            coEvery { dataSource.findLoanRecordTransaction(loanRecordId) } returns null

            //when
            val res = repository.findLoanRecordTransaction(loanRecordId)

            //then
            res shouldBe null
        }

        "valid transaction entity" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()

            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val transaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = transactionId,
                dueDate = startDate,
                categoryId = null,
                recurringRuleId = recurringRuleId,
                loanRecordId = loanRecordId
            )

            coEvery { dataSource.findLoanRecordTransaction(loanRecordId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findLoanRecordTransaction(loanRecordId)

            //then
            res shouldBe Income(
                id = TransactionId(transactionId),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, null, loanRecordId),
                lastUpdated = toInstant(startDate),
                removed = false,
                value = Value(PositiveDouble(100.0), account.asset),
                account = account.id
            )
        }

        "invalid transaction entity" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()

            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val transaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = endDate,
                id = transactionId,
                dueDate = startDate,
                categoryId = null,
                loanRecordId = loanRecordId
            )


            coEvery { dataSource.findLoanRecordTransaction(loanRecordId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findLoanRecordTransaction(loanRecordId)

            //then
            res shouldBe null
        }
    }

    "find all by loan id" - {
        "empty transactions" {
            //given
            val repository = newRepository()
            val loanId = UUID.randomUUID()
            coEvery { dataSource.findAllByLoanId(loanId) } returns emptyList()

            //when
            val res = repository.findAllByLoanId(loanId)

            //then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            //given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val invalidTransactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()
            val loanId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString("Bank"),
                asset = AssetCode("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val validTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 1",
                description = "Desc",
                dateTime = startDate,
                id = validTransactionId,
                dueDate = startDate,
                categoryId = categoryId,
                loanId = loanId
            )

            val invalidTransaction = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = -100.0,
                title = "Transaction 2",
                description = "Desc",
                dateTime = endDate,
                id = validTransaction2Id,
                dueDate = startDate,
                categoryId = categoryId,
                loanId = loanId
            )

            val invalidTransaction2 = TransactionEntity(
                accountId = accountId,
                type = TransactionType.INCOME,
                amount = 100.0,
                title = "Transaction 3",
                description = "",
                dateTime = endDate,
                id = invalidTransactionId,
                dueDate = endDate,
                categoryId = categoryId,
                loanId = loanId
            )

            coEvery { dataSource.findAllByLoanId(loanId) } returns listOf(
                validTransaction,
                invalidTransaction2,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            //when
            val res = repository.findAllByLoanId(loanId)

            //then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = CategoryId(categoryId),
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, loanId, null),
                    lastUpdated = toInstant(startDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), account.asset),
                    account = account.id
                )
            )
        }
    }

    "save" {
        //given
        val repository = newRepository()
        val accountId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val transactionDate = LocalDateTime.now()
        coEvery { dataSource.save(any()) } just runs

        //when
        repository.save(
            AccountId(accountId),
            Income(
                id = TransactionId(transactionId),
                title = NotBlankTrimmedString("Transaction 1"),
                description = NotBlankTrimmedString("Desc"),
                category = null,
                time = toInstant(transactionDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = toInstant(transactionDate),
                removed = false,
                value = Value(PositiveDouble(100.0), AssetCode("NGN")),
                account = AccountId(accountId)
            )
        )

        //then
        coVerify(exactly = 1) {
            dataSource.save(
                TransactionEntity(
                    accountId = accountId,
                    type = TransactionType.INCOME,
                    amount = 100.0,
                    title = "Transaction 1",
                    description = "Desc",
                    dateTime = transactionDate,
                    id = transactionId,
                    toAmount = 100.0
                )
            )
        }
    }

    "save many" {
        //given
        val repository = newRepository()
        val accountId = UUID.randomUUID()
        val transaction1Id = UUID.randomUUID()
        val transaction2Id = UUID.randomUUID()
        val transactionDate = LocalDateTime.now()
        coEvery { dataSource.saveMany(any()) } just runs

        //when
        repository.saveMany(
            AccountId(accountId),
            listOf(
                Income(
                    id = TransactionId(transaction1Id),
                    title = NotBlankTrimmedString("Transaction 1"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(transactionDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(transactionDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), AssetCode("NGN")),
                    account = AccountId(accountId)
                ),
                Expense(
                    id = TransactionId(transaction2Id),
                    title = NotBlankTrimmedString("Transaction 2"),
                    description = NotBlankTrimmedString("Desc"),
                    category = null,
                    time = toInstant(transactionDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = toInstant(transactionDate),
                    removed = false,
                    value = Value(PositiveDouble(100.0), AssetCode("NGN")),
                    account = AccountId(accountId)
                ),
            )
        )

        //then
        coVerify(exactly = 1) {
            dataSource.saveMany(
                listOf(
                    TransactionEntity(
                        accountId = accountId,
                        type = TransactionType.INCOME,
                        amount = 100.0,
                        title = "Transaction 1",
                        description = "Desc",
                        dateTime = transactionDate,
                        id = transaction1Id,
                        toAmount = 100.0
                    ),
                    TransactionEntity(
                        accountId = accountId,
                        type = TransactionType.EXPENSE,
                        amount = 100.0,
                        title = "Transaction 2",
                        description = "Desc",
                        dateTime = transactionDate,
                        id = transaction2Id,
                        toAmount = 100.0
                    )
                )
            )
        }
    }

    "flag deleted" {
        //given
        val repository = newRepository()
        val transactionId = UUID.randomUUID()
        coEvery { dataSource.flagDeleted(any()) } just runs

        //when
        repository.flagDeleted(TransactionId(transactionId))

        //then
        coVerify(exactly = 1) {
            dataSource.flagDeleted(transactionId)
        }
    }

    "flag deleted by recurring rule id and no date time" {
        //given
        val repository = newRepository()
        val recurringRuleId = UUID.randomUUID()
        coEvery { dataSource.flagDeletedByRecurringRuleIdAndNoDateTime(any()) } just runs

        //when
        repository.flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId)

        //then
        coVerify(exactly = 1) {
            dataSource.flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId)
        }
    }

    "flag deleted by account id" {
        //given
        val repository = newRepository()
        val accountId = UUID.randomUUID()
        coEvery { dataSource.flagDeletedByAccountId(any()) } just runs

        //when
        repository.flagDeletedByAccountId(AccountId(accountId))

        //then
        coVerify(exactly = 1) {
            dataSource.flagDeletedByAccountId(accountId)
        }
    }

    "delete by id" {
        //given
        val repository = newRepository()
        val transactionId = UUID.randomUUID()
        coEvery { dataSource.deleteById(any()) } just runs

        //when
        repository.deleteById(TransactionId(transactionId))

        //then
        coVerify(exactly = 1) {
            dataSource.deleteById(transactionId)
        }
    }

    "delete all by account id" {
        //given
        val repository = newRepository()
        val accountId = UUID.randomUUID()
        coEvery { dataSource.deleteAllByAccountId(any()) } just runs

        //when
        repository.deleteAllByAccountId(AccountId(accountId))

        //then
        coVerify(exactly = 1) {
            dataSource.deleteAllByAccountId(accountId)
        }
    }

    "delete all" {
        //given
        val repository = newRepository()
        coEvery { dataSource.deleteAll() } just runs

        //when
        repository.deleteAll()

        //then
        coVerify(exactly = 1) {
            dataSource.deleteAll()
        }
    }
})