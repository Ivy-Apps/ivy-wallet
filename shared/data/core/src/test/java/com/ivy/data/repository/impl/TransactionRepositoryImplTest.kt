package com.ivy.data.repository.impl

import arrow.core.Either
import com.ivy.base.TestDispatchersProvider
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.invalidTransactionEntity
import com.ivy.data.model.Transaction
import com.ivy.data.model.testing.ModelFixtures
import com.ivy.data.model.testing.transaction
import com.ivy.data.repository.TagsRepository
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.data.validTransactionEntity
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.mockk.coEvery
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
        repository = TransactionRepositoryImpl(
            mapper = mapper,
            transactionDao = transactionDao,
            writeTransactionDao = writeTransactionDao,
            dispatchersProvider = TestDispatchersProvider,
            tagRepository = tagRepository
        )
    }

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

    private fun transactionsTestCase(
        daoMethod: suspend () -> List<TransactionEntity>,
        repoMethod: suspend () -> List<Transaction>,
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
            val expectedTrns = trnMapping.values.mapNotNull { it.getOrNull() }.toSet()
            trns.toSet() shouldBe expectedTrns
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