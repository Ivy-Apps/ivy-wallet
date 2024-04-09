package com.ivy.data.repository.impl

import arrow.core.Either
import arrow.core.identity
import com.ivy.base.TestDispatchersProvider
import com.ivy.base.model.TransactionType
import com.ivy.data.db.dao.fake.FakeTransactionDao
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.invalidTransactionEntity
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.model.testing.ModelFixtures
import com.ivy.data.model.testing.transaction
import com.ivy.data.model.testing.transactionId
import com.ivy.data.repository.TagsRepository
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.data.validTransactionEntity
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.localDateTime
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

typealias TrnMappingRow = Pair<TransactionEntity, Either<String, Transaction>>

class TransactionRepositoryImplTest {

    private val mapper = mockk<TransactionMapper>()
    private val transactionDao = mockk<TransactionDao>()
    private val writeTransactionDao = mockk<WriteTransactionDao>()
    private val tagRepository = mockk<TagsRepository>(relaxed = true)

    private lateinit var repository: TransactionRepository

    @Before
    fun setup() {
        repository = newRepository(fakeDao = null)
    }

    private fun newRepository(
        fakeDao: FakeTransactionDao?,
    ): TransactionRepository = TransactionRepositoryImpl(
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
        val startDate = Arb.localDateTime().next()
        val endDate = Arb.localDateTime().next()

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
        val startDate = Arb.localDateTime().next()
        val endDate = Arb.localDateTime().next()

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
        val startDate = Arb.localDateTime().next()
        val endDate = Arb.localDateTime().next()

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
        val startDate = Arb.localDateTime().next()
        val endDate = Arb.localDateTime().next()

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
        val startDate = Arb.localDateTime().next()
        val endDate = Arb.localDateTime().next()

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
        val startDate = Arb.localDateTime().next()
        val endDate = Arb.localDateTime().next()

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
    fun save() = runTest {
        // given
        val trn = Arb.transaction().next()
        val entity = mockk<TransactionEntity>(relaxed = true) {
            every { id } returns trn.id.value
        }
        repository = newRepository(fakeDao = FakeTransactionDao())
        with(mapper) {
            every { trn.toEntity() } returns entity
            coEvery { entity.toDomain(any()) } returns Either.Right(trn)
        }

        // when
        repository.save(trn)

        // then
        val savedTrn = repository.findById(trn.id)
        savedTrn shouldBe trn
    }

    @Test
    fun saveMany() = runTest {
        // given
        val trn1 = Arb.transaction().next()
        val trn2 = Arb.transaction().next()
        val entity1 = mockk<TransactionEntity>(relaxed = true) {
            every { id } returns trn1.id.value
        }
        val entity2 = mockk<TransactionEntity>(relaxed = true) {
            every { id } returns trn2.id.value
        }
        repository = newRepository(fakeDao = FakeTransactionDao())
        with(mapper) {
            every { trn1.toEntity() } returns entity1
            coEvery { entity1.toDomain(any()) } returns Either.Right(trn1)
            every { trn2.toEntity() } returns entity2
            coEvery { entity2.toDomain(any()) } returns Either.Right(trn2)
        }

        // when
        repository.saveMany(listOf(trn1, trn2))

        // then
        val savedTrns = repository.findAll()
        savedTrns.toSet() shouldBe setOf(trn1, trn2)
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