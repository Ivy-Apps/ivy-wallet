package com.ivy.data.repository

import arrow.core.Either
import arrow.core.Some
import arrow.core.identity
import com.ivy.base.TestDispatchersProvider
import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.fake.FakeTransactionDao
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.invalidTransactionEntity
import com.ivy.data.model.AccountId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.model.testing.ModelFixtures
import com.ivy.data.model.testing.accountId
import com.ivy.data.model.testing.transaction
import com.ivy.data.model.testing.transactionId
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.data.validTransactionEntity
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.instant
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TransactionRepositoryTest {

    private val mapper = mockk<TransactionMapper>()
    private val transactionDao = mockk<TransactionDao>()
    private val writeTransactionDao = mockk<WriteTransactionDao>()
    private val tagRepository = mockk<TagRepository>(relaxed = true)

    private lateinit var repository: TransactionRepository

    @Before
    fun setup() {
        repository = newRepository(fakeDao = null)
    }

    private fun newRepository(
        fakeDao: FakeTransactionDao?,
    ): TransactionRepository = TransactionRepository(
        mapper = mapper,
        transactionDao = fakeDao ?: transactionDao,
        writeTransactionDao = fakeDao ?: writeTransactionDao,
        dispatchersProvider = TestDispatchersProvider,
        tagRepository = tagRepository
    )

    @Test
    fun `find by id - not existing`() = runTest {
        // given
        val transactionId = ModelFixtures.TransactionId
        coEvery {
            transactionDao.findById(transactionId.value)
        } returns null

        // when
        val trn = repository.findById(transactionId)

        // then
        trn shouldBe null
    }

    @Test
    fun `find by id - existing, successful mapping`() = runTest {
        // given
        val transactionId = ModelFixtures.TransactionId
        val entity = mockk<TransactionEntity>()
        val transaction = mockk<Transaction>()
        coEvery {
            transactionDao.findById(transactionId.value)
        } returns entity
        with(mapper) {
            coEvery { entity.toDomain(any()) } returns Either.Right(transaction)
        }

        // when
        val trn = repository.findById(transactionId)

        // then
        trn shouldBe transaction
    }

    @Test
    fun `find by id - existing, failed mapping`() = runTest {
        // given
        val transactionId = ModelFixtures.TransactionId
        val entity = mockk<TransactionEntity>()
        coEvery {
            transactionDao.findById(transactionId.value)
        } returns entity
        with(mapper) {
            coEvery { entity.toDomain(any()) } returns Either.Left("err")
        }

        // when
        val trn = repository.findById(transactionId)

        // then
        trn shouldBe null
    }

    @Test
    fun `find all`() = transactionsTestCase(
        daoMethod = transactionDao::findAll,
        repoMethod = repository::findAll
    )

    @Test
    fun findAllIncomeByAccount() {
        val account = ModelFixtures.AccountId

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllByTypeAndAccount(
                    type = TransactionType.INCOME,
                    accountId = account.value
                )
            },
            repoMethod = {
                repository.findAllIncomeByAccount(account)
            },
            mapExpectedResult = { it.filterIsInstance<Income>() }
        )
    }

    @Test
    fun findAllExpenseByAccount() {
        val account = ModelFixtures.AccountId

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllByTypeAndAccount(
                    type = TransactionType.EXPENSE,
                    accountId = account.value
                )
            },
            repoMethod = {
                repository.findAllExpenseByAccount(account)
            },
            mapExpectedResult = { it.filterIsInstance<Expense>() }
        )
    }

    @Test
    fun findAllTransferByAccount() {
        val account = ModelFixtures.AccountId

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllByTypeAndAccount(
                    type = TransactionType.TRANSFER,
                    accountId = account.value
                )
            },
            repoMethod = {
                repository.findAllTransferByAccount(account)
            },
            mapExpectedResult = { it.filterIsInstance<Transfer>() }
        )
    }

    @Test
    fun findAllTransfersToAccount() {
        val account = ModelFixtures.AccountId

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllTransfersToAccount(
                    toAccountId = account.value
                )
            },
            repoMethod = {
                repository.findAllTransfersToAccount(account)
            },
            mapExpectedResult = { it.filterIsInstance<Transfer>() }
        )
    }

    @Test
    fun `find all by ids`() {
        val ids = Arb.list(Arb.transactionId()).next()
        transactionsTestCase(
            daoMethod = {
                transactionDao.findByIds(ids.map { it.value })
            },
            repoMethod = {
                repository.findByIds(ids)
            }
        )
    }

    @Test
    fun `find all between`() {
        val startDate = Arb.instant().next()
        val endDate = Arb.instant().next()

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllBetween(
                    startDate = startDate,
                    endDate = endDate,
                )
            },
            repoMethod = {
                repository.findAllBetween(
                    startDate = startDate,
                    endDate = endDate,
                )
            }
        )
    }

    @Test
    fun findAllByAccountAndBetween() {
        val account = ModelFixtures.AccountId
        val startDate = Arb.instant().next()
        val endDate = Arb.instant().next()

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllByAccountAndBetween(
                    accountId = account.value,
                    startDate = startDate,
                    endDate = endDate,
                )
            },
            repoMethod = {
                repository.findAllByAccountAndBetween(
                    accountId = account,
                    startDate = startDate,
                    endDate = endDate,
                )
            }
        )
    }

    @Test
    fun findAllToAccountAndBetween() {
        val account = ModelFixtures.AccountId
        val startDate = Arb.instant().next()
        val endDate = Arb.instant().next()

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllToAccountAndBetween(
                    toAccountId = account.value,
                    startDate = startDate,
                    endDate = endDate,
                )
            },
            repoMethod = {
                repository.findAllToAccountAndBetween(
                    toAccountId = account,
                    startDate = startDate,
                    endDate = endDate,
                )
            }
        )
    }

    @Test
    fun findAllDueToBetweenByCategory() {
        val category = ModelFixtures.CategoryId
        val startDate = Arb.instant().next()
        val endDate = Arb.instant().next()

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllDueToBetweenByCategory(
                    categoryId = category.value,
                    startDate = startDate,
                    endDate = endDate,
                )
            },
            repoMethod = {
                repository.findAllDueToBetweenByCategory(
                    categoryId = category,
                    startDate = startDate,
                    endDate = endDate,
                )
            }
        )
    }

    @Test
    fun findAllDueToBetweenByCategoryUnspecified() {
        val startDate = Arb.instant().next()
        val endDate = Arb.instant().next()

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllDueToBetweenByCategoryUnspecified(
                    startDate = startDate,
                    endDate = endDate,
                )
            },
            repoMethod = {
                repository.findAllDueToBetweenByCategoryUnspecified(
                    startDate = startDate,
                    endDate = endDate,
                )
            }
        )
    }

    @Test
    fun findAllDueToBetweenByAccount() {
        val account = ModelFixtures.AccountId
        val startDate = Arb.instant().next()
        val endDate = Arb.instant().next()

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllDueToBetweenByAccount(
                    accountId = account.value,
                    startDate = startDate,
                    endDate = endDate,
                )
            },
            repoMethod = {
                repository.findAllDueToBetweenByAccount(
                    accountId = account,
                    startDate = startDate,
                    endDate = endDate,
                )
            }
        )
    }

    @Test
    fun findAllByCategoryAndTypeAndBetween() {
        val categoryId = ModelFixtures.CategoryId
        val trnType = TransactionType.EXPENSE
        val startDate = Arb.instant().next()
        val endDate = Arb.instant().next()

        transactionsTestCase(
            daoMethod = {
                transactionDao.findAllByCategoryAndTypeAndBetween(
                    categoryId = categoryId.value,
                    type = trnType,
                    startDate = startDate,
                    endDate = endDate,
                )
            },
            repoMethod = {
                repository.findAllByCategoryAndTypeAndBetween(
                    categoryId = categoryId.value,
                    type = trnType,
                    startDate = startDate,
                    endDate = endDate,
                )
            }
        )
    }

    @Test
    fun save() = runTest {
        // given
        repository = newRepository(fakeDao = FakeTransactionDao())
        val trn = mockkFakeTrnMapping()

        // when
        repository.save(trn)

        // then
        val savedTrn = repository.findById(trn.id)
        savedTrn shouldBe trn
    }

    @Test
    fun saveMany() = runTest {
        // given
        repository = newRepository(fakeDao = FakeTransactionDao())
        val trn1 = mockkFakeTrnMapping()
        val trn2 = mockkFakeTrnMapping()

        // when
        repository.saveMany(listOf(trn1, trn2))

        // then
        val savedTrns = repository.findAll()
        savedTrns.toSet() shouldBe setOf(trn1, trn2)
    }

    @Test
    fun deleteById() = runTest {
        // given
        repository = newRepository(fakeDao = FakeTransactionDao())
        val trn = mockkFakeTrnMapping()
        repository.save(trn)

        // when
        repository.deleteById(trn.id)

        // then
        repository.findById(trn.id) shouldBe null
    }

    @Test
    fun deleteAllByAccountId() = runTest {
        // given
        repository = newRepository(fakeDao = FakeTransactionDao())
        val acc1 = Arb.accountId().next()
        val acc2 = Arb.accountId().next()
        val trnOneAcc1 = mockkFakeTrnMapping(account = acc1)
        val trnTwoAcc1 = mockkFakeTrnMapping(account = acc1)
        val trnAcc2 = mockkFakeTrnMapping(account = acc2)
        repository.saveMany(listOf(trnOneAcc1, trnTwoAcc1, trnAcc2))

        // when
        repository.deleteAllByAccountId(accountId = acc1)

        // then
        repository.findAll() shouldBe listOf(trnAcc2)
    }

    @Test
    fun countNumberOfTransactions() = runTest {
        // given
        repository = newRepository(fakeDao = FakeTransactionDao())

        repository.countHappenedTransactions().value shouldBeGreaterThanOrEqual 0L
    }

    @Test
    fun deleteAll() = runTest {
        // given
        repository = newRepository(fakeDao = FakeTransactionDao())
        val trn1 = mockkFakeTrnMapping()
        val trn2 = mockkFakeTrnMapping()
        val trn3 = mockkFakeTrnMapping()
        repository.saveMany(listOf(trn1, trn2, trn3))

        // when
        repository.deleteAll()

        // then
        repository.findAll() shouldBe emptyList()
    }

    private fun mockkFakeTrnMapping(
        account: AccountId = Arb.accountId().next()
    ): Transaction {
        val trn = Arb.transaction(account = Some(account)).next()
        val entity = mockk<TransactionEntity>(relaxed = true) {
            every { id } returns trn.id.value
            every { accountId } returns account.value
        }
        with(mapper) {
            every { trn.toEntity() } returns entity
            coEvery { entity.toDomain(any()) } returns Either.Right(trn)
        }
        return trn
    }

    private fun transactionsTestCase(
        daoMethod: suspend () -> List<TransactionEntity>,
        repoMethod: suspend () -> List<Transaction>,
        mapExpectedResult: (List<Transaction>) -> List<Transaction> = ::identity
    ) = runTest {
        checkAll(
            Arb.map(
                arb = Arb.trnMappingRow(),
                minSize = 0,
                maxSize = 10,
            )
        ) { trnMapping ->
            // given
            coEvery { daoMethod() } returns trnMapping.keys.toList()
            trnMapping.forEach { (entity, mappingRes) ->
                with(mapper) {
                    coEvery { entity.toDomain(any()) } returns mappingRes
                }
            }

            // when
            val trns = repoMethod()

            // then
            val expectedTrns = trnMapping.values.mapNotNull { it.getOrNull() }
            trns.toSet() shouldBe mapExpectedResult(expectedTrns).toSet()
        }
    }

    private fun Arb.Companion.trnMappingRow(): Arb<TrnMappingRow> = arbitrary {
        val isValid = Arb.boolean().bind()
        if (isValid) {
            Arb.validTransactionEntity().bind() to Either.Right(Arb.transaction().bind())
        } else {
            Arb.invalidTransactionEntity().bind() to Either.Left(Arb.string().bind())
        }
    }
}

typealias TrnMappingRow = Pair<TransactionEntity, Either<String, Transaction>>