package com.ivy.data.repository.impl

import com.ivy.base.TestDispatchersProvider
import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteTransactionDao
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
import com.ivy.data.model.primitive.AssociationId
import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.TagsRepository
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TransactionMapper
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.collections.immutable.persistentListOf
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID

@Suppress("LargeClass")
class TransactionRepositoryImplTest : FreeSpec({
    val transactionDao = mockk<TransactionDao>()
    val writeTransactionDao = mockk<WriteTransactionDao>()
    val accountRepo = mockk<AccountRepository>()
    val mapper = TransactionMapper()
    val tagsRepo = mockk<TagsRepository>()

    fun newRepository(): TransactionRepository = TransactionRepositoryImpl(
        accountRepository = accountRepo,
        mapper = mapper,
        transactionDao = transactionDao,
        writeTransactionDao = writeTransactionDao,
        dispatchersProvider = TestDispatchersProvider,
        tagRepository = tagsRepo
    )

    fun toInstant(localDateTime: LocalDateTime): Instant {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant()
    }

    "find all" - {
        "empty transactions" {
            //  given
            val repository = newRepository()
            coEvery { transactionDao.findAll() } returns emptyList()
            coEvery { tagsRepo.findByAllTagsForAssociations() } returns emptyMap()

            //  when
            val res = repository.findAll()

            //  then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
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
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val toAccount = Account(
                id = AccountId(toAccountId),
                name = NotBlankTrimmedString.unsafe("Cash"),
                asset = AssetCode.unsafe("NGN"),
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
                amount = 0.0,
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
                amount = 0.0,
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
                amount = 0.0,
                toAccountId = toAccountId,
                title = " ",
                toAmount = 100.0,
                description = "Desc",
                dateTime = transactionDateTime,
                id = invalidTransferId
            )

            coEvery { transactionDao.findAll() } returns listOf(
                validIncome,
                invalidIncome,
                validExpense,
                invalidExpense,
                validTransfer,
                invalidTransfer
            )
            coEvery { accountRepo.findById(account.id) } returns account
            coEvery { accountRepo.findById(toAccount.id) } returns toAccount
            coEvery { tagsRepo.findByAllTagsForAssociations() } returns emptyMap()

            // when
            val res = repository.findAll()

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(transactionDateTime),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = AccountId(accountId),
                    tags = persistentListOf()
                ),
                Expense(
                    id = TransactionId(validExpenseId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(transactionDateTime),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = AccountId(accountId),
                    tags = persistentListOf()
                ),
                Transfer(
                    id = TransactionId(validTransferId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(transactionDateTime),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    fromAccount = AccountId(accountId),
                    fromValue = Value(PositiveDouble.unsafe(100.0), account.asset),
                    toAccount = AccountId(toAccountId),
                    toValue = Value(PositiveDouble.unsafe(100.0), toAccount.asset),
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all limit 1" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            coEvery { transactionDao.findAll_LIMIT_1() } returns emptyList()

            // when
            val res = repository.findAll_LIMIT_1()

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val validIncomeId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val transactionDateTime = LocalDateTime.now()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery { transactionDao.findAll_LIMIT_1() } returns listOf(validIncome)
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findAll_LIMIT_1()

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(transactionDateTime),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all between" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery { transactionDao.findAllBetween(startDate, endDate) } returns emptyList()
            coEvery { tagsRepo.findByAssociatedId(listOf()) } returns emptyMap()

            // when
            val res = repository.findAllBetween(startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validIncomeId = UUID.randomUUID()
            val validIncome2Id = UUID.randomUUID()
            val invalidIncomeId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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
                amount = 0.0,
                toAccountId = null,
                title = "",
                toAmount = 100.0,
                description = "Desc",
                dateTime = endDate,
                id = invalidIncomeId
            )

            coEvery { transactionDao.findAllBetween(startDate, endDate) } returns listOf(
                validIncome,
                validIncome2,
                invalidIncome
            )
            coEvery {
                tagsRepo.findByAssociatedId(
                    listOf(
                        AssociationId(validIncome.id),
                        AssociationId(validIncome2.id),
                        AssociationId(invalidIncome.id)
                    )
                )
            } returns emptyMap()
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findAllBetween(startDate, endDate)

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                ),
                Income(
                    id = TransactionId(validIncome2Id),
                    title = NotBlankTrimmedString.unsafe("Transaction 2"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(endDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all by account and between" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val accountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                transactionDao.findAllByAccountAndBetween(
                    accountId,
                    startDate,
                    endDate
                )
            } returns emptyList()

            // when
            val res =
                repository.findAllByAccountAndBetween(AccountId(accountId), startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validIncomeId = UUID.randomUUID()
            val validIncome2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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
                amount = 0.0,
                toAccountId = null,
                title = "",
                toAmount = 100.0,
                description = "",
                dateTime = endDate,
                id = validIncome2Id
            )

            coEvery {
                transactionDao.findAllByAccountAndBetween(
                    accountId,
                    startDate,
                    endDate
                )
            } returns listOf(
                validIncome,
                invalidIncome
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res =
                repository.findAllByAccountAndBetween(AccountId(accountId), startDate, endDate)

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all by category and between" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val categoryId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                transactionDao.findAllByCategoryAndBetween(
                    categoryId,
                    startDate,
                    endDate
                )
            } returns emptyList()

            // when
            val res =
                repository.findAllByCategoryAndBetween(CategoryId(categoryId), startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validIncomeId = UUID.randomUUID()
            val invalidIncomeId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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
                transactionDao.findAllByCategoryAndBetween(
                    categoryId,
                    startDate,
                    endDate
                )
            } returns listOf(
                validIncome,
                invalidIncome,
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res =
                repository.findAllByCategoryAndBetween(CategoryId(categoryId), startDate, endDate)

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = CategoryId(categoryId),
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all unspecified and between" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                transactionDao.findAllUnspecifiedAndBetween(
                    startDate,
                    endDate
                )
            } returns emptyList()

            // when
            val res =
                repository.findAllUnspecifiedAndBetween(startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validIncomeId = UUID.randomUUID()
            val validIncome2Id = UUID.randomUUID()
            val invalidIncomeId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery {
                transactionDao.findAllUnspecifiedAndBetween(
                    startDate,
                    endDate
                )
            } returns listOf(
                validIncome,
                invalidIncome2,
                invalidIncome
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findAllUnspecifiedAndBetween(startDate, endDate)

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validIncomeId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all to account and between" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val toAccountId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                transactionDao.findAllToAccountAndBetween(
                    toAccountId,
                    startDate,
                    endDate
                )
            } returns emptyList()

            // when
            val res =
                repository.findAllToAccountAndBetween(AccountId(toAccountId), startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val toAccount = Account(
                id = AccountId(toAccountId),
                name = NotBlankTrimmedString.unsafe("Cash"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery {
                transactionDao.findAllToAccountAndBetween(
                    toAccountId,
                    startDate,
                    endDate
                )
            } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account
            coEvery { accountRepo.findById(toAccount.id) } returns toAccount

            // when
            val res =
                repository.findAllToAccountAndBetween(AccountId(toAccountId), startDate, endDate)

            // then
            res shouldBe listOf(
                Transfer(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    fromAccount = AccountId(accountId),
                    fromValue = Value(PositiveDouble.unsafe(100.0), account.asset),
                    toAccount = AccountId(toAccountId),
                    toValue = Value(PositiveDouble.unsafe(100.0), account.asset),
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all due to and between" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                transactionDao.findAllDueToBetween(
                    startDate,
                    endDate
                )
            } returns emptyList()

            // when
            val res =
                repository.findAllDueToBetween(startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val toAccountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
                color = ColorInt(1),
                icon = null,
                includeInBalance = true,
                orderNum = 0.0,
                lastUpdated = Instant.now(),
                removed = false
            )

            val toAccount = Account(
                id = AccountId(toAccountId),
                name = NotBlankTrimmedString.unsafe("Cash"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery {
                transactionDao.findAllDueToBetween(
                    startDate,
                    endDate
                )
            } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account
            coEvery { accountRepo.findById(toAccount.id) } returns toAccount

            // when
            val res =
                repository.findAllDueToBetween(startDate, endDate)

            // then
            res shouldBe listOf(
                Transfer(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    fromAccount = AccountId(accountId),
                    fromValue = Value(PositiveDouble.unsafe(100.0), account.asset),
                    toAccount = AccountId(toAccountId),
                    toValue = Value(PositiveDouble.unsafe(100.0), account.asset),
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all due to between by category" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val categoryId = UUID.randomUUID()
            coEvery {
                transactionDao.findAllDueToBetweenByCategory(
                    startDate,
                    endDate,
                    categoryId
                )
            } returns emptyList()

            // when
            val res =
                repository.findAllDueToBetweenByCategory(startDate, endDate, CategoryId(categoryId))

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery {
                transactionDao.findAllDueToBetweenByCategory(
                    startDate,
                    endDate,
                    categoryId
                )
            } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res =
                repository.findAllDueToBetweenByCategory(startDate, endDate, CategoryId(categoryId))

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = CategoryId(categoryId),
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all due to between by unspecified category" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                transactionDao.findAllDueToBetweenByCategoryUnspecified(
                    startDate,
                    endDate
                )
            } returns emptyList()

            // when
            val res =
                repository.findAllDueToBetweenByCategoryUnspecified(startDate, endDate)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery {
                transactionDao.findAllDueToBetweenByCategoryUnspecified(
                    startDate,
                    endDate,
                )
            } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res =
                repository.findAllDueToBetweenByCategoryUnspecified(startDate, endDate)

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all due to between by account" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val accountId = UUID.randomUUID()
            coEvery {
                transactionDao.findAllDueToBetweenByAccount(
                    startDate,
                    endDate,
                    accountId
                )
            } returns emptyList()

            // when
            val res =
                repository.findAllDueToBetweenByAccount(startDate, endDate, AccountId(accountId))

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery {
                transactionDao.findAllDueToBetweenByAccount(
                    startDate,
                    endDate,
                    accountId
                )
            } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res =
                repository.findAllDueToBetweenByAccount(startDate, endDate, AccountId(accountId))

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all by recurring rule id" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val recurringRuleId = UUID.randomUUID()
            coEvery {
                transactionDao.findAllByRecurringRuleId(recurringRuleId)
            } returns emptyList()

            // when
            val res = repository.findAllByRecurringRuleId(recurringRuleId)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery {
                transactionDao.findAllByRecurringRuleId(recurringRuleId)
            } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findAllByRecurringRuleId(recurringRuleId)

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(recurringRuleId, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all between and recurring rule id" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val recurringRuleId = UUID.randomUUID()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            coEvery {
                transactionDao.findAllBetweenAndRecurringRuleId(
                    startDate,
                    endDate,
                    recurringRuleId
                )
            } returns emptyList()

            // when
            val res =
                repository.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery {
                transactionDao.findAllBetweenAndRecurringRuleId(
                    startDate,
                    endDate,
                    recurringRuleId
                )
            } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res =
                repository.findAllBetweenAndRecurringRuleId(startDate, endDate, recurringRuleId)

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(recurringRuleId, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find by id" - {
        "null transaction entity" {
            // given
            val repository = newRepository()
            val transactionId = UUID.randomUUID()
            coEvery { transactionDao.findById(transactionId) } returns null

            // when
            val res =
                repository.findById(TransactionId(transactionId))

            // then
            res shouldBe null
        }

        "valid transaction entity" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery { transactionDao.findById(transactionId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findById(TransactionId(transactionId))

            // then
            res shouldBe Income(
                id = TransactionId(transactionId),
                title = NotBlankTrimmedString.unsafe("Transaction 1"),
                description = NotBlankTrimmedString.unsafe("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble.unsafe(100.0), account.asset),
                account = account.id,
                tags = persistentListOf()
            )
        }

        "invalid transaction entity" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery { transactionDao.findById(transactionId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findById(TransactionId(transactionId))

            // then
            res shouldBe null
        }
    }

    "find by is synced and is deleted" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val isSynced = true
            val isDeleted = true
            coEvery {
                transactionDao.findByIsSyncedAndIsDeleted(
                    synced = isSynced,
                    deleted = isDeleted,
                )
            } returns emptyList()

            // when
            val res =
                repository.findByIsSyncedAndIsDeleted(isSynced, isDeleted)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val isSynced = true
            val isDeleted = true
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery {
                transactionDao.findByIsSyncedAndIsDeleted(
                    isSynced,
                    isDeleted
                )
            } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findByIsSyncedAndIsDeleted(isSynced, isDeleted)

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = isDeleted,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all by category" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val categoryId = UUID.randomUUID()
            coEvery { transactionDao.findAllByCategory(categoryId) } returns emptyList()

            // when
            val res = repository.findAllByCategory(CategoryId(categoryId))

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery { transactionDao.findAllByCategory(categoryId) } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findAllByCategory(CategoryId(categoryId))

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = CategoryId(categoryId),
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find all by account" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val accountId = UUID.randomUUID()
            coEvery { transactionDao.findAllByAccount(accountId) } returns emptyList()

            // when
            val res = repository.findAllByAccount(AccountId(accountId))

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery { transactionDao.findAllByAccount(accountId) } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findAllByAccount(AccountId(accountId))

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = CategoryId(categoryId),
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "find by loan id" - {
        "null transaction entity" {
            // given
            val repository = newRepository()
            val loanId = UUID.randomUUID()
            coEvery { transactionDao.findLoanTransaction(loanId) } returns null

            // when
            val res = repository.findLoanTransaction(loanId)

            // then
            res shouldBe null
        }

        "valid transaction entity" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val loanId = UUID.randomUUID()

            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery { transactionDao.findLoanTransaction(loanId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findLoanTransaction(loanId)

            // then
            res shouldBe Income(
                id = TransactionId(transactionId),
                title = NotBlankTrimmedString.unsafe("Transaction 1"),
                description = NotBlankTrimmedString.unsafe("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, loanId, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble.unsafe(100.0), account.asset),
                account = account.id,
                tags = persistentListOf()
            )
        }

        "invalid transaction entity" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val loanId = UUID.randomUUID()

            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery { transactionDao.findLoanTransaction(loanId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findLoanTransaction(loanId)

            // then
            res shouldBe null
        }
    }

    "find by loan record id" - {
        "null transaction entity" {
            // given
            val repository = newRepository()
            val loanRecordId = UUID.randomUUID()
            coEvery { transactionDao.findLoanRecordTransaction(loanRecordId) } returns null

            // when
            val res = repository.findLoanRecordTransaction(loanRecordId)

            // then
            res shouldBe null
        }

        "valid transaction entity" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val recurringRuleId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()

            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery { transactionDao.findLoanRecordTransaction(loanRecordId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findLoanRecordTransaction(loanRecordId)

            // then
            res shouldBe Income(
                id = TransactionId(transactionId),
                title = NotBlankTrimmedString.unsafe("Transaction 1"),
                description = NotBlankTrimmedString.unsafe("Desc"),
                category = null,
                time = toInstant(startDate),
                settled = true,
                metadata = TransactionMetadata(recurringRuleId, null, loanRecordId),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble.unsafe(100.0), account.asset),
                account = account.id,
                tags = persistentListOf()
            )
        }

        "invalid transaction entity" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val transactionId = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val loanRecordId = UUID.randomUUID()

            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery { transactionDao.findLoanRecordTransaction(loanRecordId) } returns transaction
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findLoanRecordTransaction(loanRecordId)

            // then
            res shouldBe null
        }
    }

    "find all by loan id" - {
        "empty transactions" {
            // given
            val repository = newRepository()
            val loanId = UUID.randomUUID()
            coEvery { transactionDao.findAllByLoanId(loanId) } returns emptyList()

            // when
            val res = repository.findAllByLoanId(loanId)

            // then
            res shouldBe emptyList()
        }

        "list with valid and invalid transactions" {
            // given
            val repository = newRepository()
            val startDate = LocalDateTime.now().minusDays(7)
            val endDate = LocalDateTime.now()
            val validTransactionId = UUID.randomUUID()
            val validTransaction2Id = UUID.randomUUID()
            val accountId = UUID.randomUUID()
            val categoryId = UUID.randomUUID()
            val loanId = UUID.randomUUID()
            val account = Account(
                id = AccountId(accountId),
                name = NotBlankTrimmedString.unsafe("Bank"),
                asset = AssetCode.unsafe("NGN"),
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

            coEvery { transactionDao.findAllByLoanId(loanId) } returns listOf(
                validTransaction,
                invalidTransaction
            )
            coEvery { accountRepo.findById(account.id) } returns account

            // when
            val res = repository.findAllByLoanId(loanId)

            // then
            res shouldBe listOf(
                Income(
                    id = TransactionId(validTransactionId),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = CategoryId(categoryId),
                    time = toInstant(startDate),
                    settled = true,
                    metadata = TransactionMetadata(null, loanId, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), account.asset),
                    account = account.id,
                    tags = persistentListOf()
                )
            )
        }
    }

    "save" {
        // given
        val repository = newRepository()
        val accountId = UUID.randomUUID()
        val transactionId = UUID.randomUUID()
        val transactionDate = LocalDateTime.now()
        coEvery { writeTransactionDao.save(any()) } just runs

        // when
        repository.save(
            AccountId(accountId),
            Income(
                id = TransactionId(transactionId),
                title = NotBlankTrimmedString.unsafe("Transaction 1"),
                description = NotBlankTrimmedString.unsafe("Desc"),
                category = null,
                time = toInstant(transactionDate),
                settled = true,
                metadata = TransactionMetadata(null, null, null),
                lastUpdated = Instant.EPOCH,
                removed = false,
                value = Value(PositiveDouble.unsafe(100.0), AssetCode.unsafe("NGN")),
                account = AccountId(accountId),
                tags = persistentListOf()
            )
        )

        // then
        coVerify(exactly = 1) {
            writeTransactionDao.save(
                TransactionEntity(
                    accountId = accountId,
                    type = TransactionType.INCOME,
                    amount = 100.0,
                    title = "Transaction 1",
                    description = "Desc",
                    dateTime = transactionDate,
                    id = transactionId,
                    toAmount = null,
                    isSynced = true
                )
            )
        }
    }

    "save many" {
        // given
        val repository = newRepository()
        val accountId = UUID.randomUUID()
        val transaction1Id = UUID.randomUUID()
        val transaction2Id = UUID.randomUUID()
        val transactionDate = LocalDateTime.now()
        coEvery { writeTransactionDao.saveMany(any()) } just runs

        // when
        repository.saveMany(
            AccountId(accountId),
            listOf(
                Income(
                    id = TransactionId(transaction1Id),
                    title = NotBlankTrimmedString.unsafe("Transaction 1"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(transactionDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), AssetCode.unsafe("NGN")),
                    account = AccountId(accountId),
                    tags = persistentListOf()
                ),
                Expense(
                    id = TransactionId(transaction2Id),
                    title = NotBlankTrimmedString.unsafe("Transaction 2"),
                    description = NotBlankTrimmedString.unsafe("Desc"),
                    category = null,
                    time = toInstant(transactionDate),
                    settled = true,
                    metadata = TransactionMetadata(null, null, null),
                    lastUpdated = Instant.EPOCH,
                    removed = false,
                    value = Value(PositiveDouble.unsafe(100.0), AssetCode.unsafe("NGN")),
                    account = AccountId(accountId),
                    tags = persistentListOf()
                ),
            )
        )

        // then
        coVerify(exactly = 1) {
            writeTransactionDao.saveMany(
                listOf(
                    TransactionEntity(
                        accountId = accountId,
                        type = TransactionType.INCOME,
                        amount = 100.0,
                        title = "Transaction 1",
                        description = "Desc",
                        dateTime = transactionDate,
                        id = transaction1Id,
                        toAmount = null,
                        isSynced = true
                    ),
                    TransactionEntity(
                        accountId = accountId,
                        type = TransactionType.EXPENSE,
                        amount = 100.0,
                        title = "Transaction 2",
                        description = "Desc",
                        dateTime = transactionDate,
                        id = transaction2Id,
                        toAmount = null,
                        isSynced = true
                    )
                )
            )
        }
    }

    "flag deleted" {
        // given
        val repository = newRepository()
        val transactionId = UUID.randomUUID()
        coEvery { writeTransactionDao.flagDeleted(any()) } just runs

        // when
        repository.flagDeleted(TransactionId(transactionId))

        // then
        coVerify(exactly = 1) {
            writeTransactionDao.flagDeleted(transactionId)
        }
    }

    "flag deleted by recurring rule id and no date time" {
        // given
        val repository = newRepository()
        val recurringRuleId = UUID.randomUUID()
        coEvery { writeTransactionDao.flagDeletedByRecurringRuleIdAndNoDateTime(any()) } just runs

        // when
        repository.flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId)

        // then
        coVerify(exactly = 1) {
            writeTransactionDao.flagDeletedByRecurringRuleIdAndNoDateTime(recurringRuleId)
        }
    }

    "flag deleted by account id" {
        // given
        val repository = newRepository()
        val accountId = UUID.randomUUID()
        coEvery { writeTransactionDao.flagDeletedByAccountId(any()) } just runs

        // when
        repository.flagDeletedByAccountId(AccountId(accountId))

        // then
        coVerify(exactly = 1) {
            writeTransactionDao.flagDeletedByAccountId(accountId)
        }
    }

    "delete by id" {
        // given
        val repository = newRepository()
        val transactionId = UUID.randomUUID()
        coEvery { writeTransactionDao.deleteById(any()) } just runs

        // when
        repository.deleteById(TransactionId(transactionId))

        // then
        coVerify(exactly = 1) {
            writeTransactionDao.deleteById(transactionId)
        }
    }

    "delete all by account id" {
        // given
        val repository = newRepository()
        val accountId = UUID.randomUUID()
        coEvery { writeTransactionDao.deleteAllByAccountId(any()) } just runs

        // when
        repository.deleteAllByAccountId(AccountId(accountId))

        // then
        coVerify(exactly = 1) {
            writeTransactionDao.deleteAllByAccountId(accountId)
        }
    }

    "delete all" {
        // given
        val repository = newRepository()
        coEvery { writeTransactionDao.deleteAll() } just runs

        // when
        repository.deleteAll()

        // then
        coVerify(exactly = 1) {
            writeTransactionDao.deleteAll()
        }
    }
})