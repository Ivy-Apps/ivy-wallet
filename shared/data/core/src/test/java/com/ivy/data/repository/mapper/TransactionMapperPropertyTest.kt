package com.ivy.data.repository.mapper

import arrow.core.Some
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.invalidIncomeOrExpense
import com.ivy.data.invalidTransfer
import com.ivy.data.model.AccountId
import com.ivy.data.model.Transfer
import com.ivy.data.model.getFromAccount
import com.ivy.data.model.getFromValue
import com.ivy.data.model.testing.account
import com.ivy.data.model.testing.transaction
import com.ivy.data.repository.AccountRepository
import com.ivy.data.validIncomeOrExpense
import com.ivy.data.validTransfer
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
import io.kotest.property.checkAll
import io.kotest.property.forAll
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TransactionMapperPropertyTest {

    private val accountRepo = mockk<AccountRepository>()

    private lateinit var mapper: TransactionMapper

    @Before
    fun setup() {
        mapper = TransactionMapper(accountRepository = accountRepo,)
    }

    @Test
    fun `property - domain-entity isomorphism`() = runTest {
        checkAll(Arb.transaction()) { trnOrig ->
            // given
            val account = Arb.account(
                accountId = Some(trnOrig.getFromAccount()),
                asset = Some(trnOrig.getFromValue().asset)
            ).next()
            coEvery { accountRepo.findById(account.id) } returns account

            if (trnOrig is Transfer) {
                val toAccount = Arb.account(
                    accountId = Some(trnOrig.toAccount),
                    asset = Some(trnOrig.toValue.asset)
                ).next()
                coEvery { accountRepo.findById(toAccount.id) } returns toAccount
            }

            with(mapper) {
                // when: domain -> entity -> domain
                val entityOne = trnOrig.toEntity()
                val trnTwo = entityOne.toDomain(tags = emptyList()).getOrNull()

                // then: the recovered domain trn must be the same
                trnTwo.shouldNotBeNull() shouldBe trnOrig

                // and when again: domain -> entity
                val entityTwo = trnTwo.toEntity()

                // then: the recovered entity must be the same
                entityTwo shouldBe entityOne
            }
        }
    }

    @Test
    fun `maps valid transfer to domain - success`() = runTest {
        forAll(Arb.validTransfer()) { entity ->
            // given
            mockkValidAccounts(entity)

            // when
            val res = with(mapper) { entity.toDomain(tags = emptyList()) }

            // then
            res.isRight()
        }
    }

    @Test
    fun `maps invalid transfer to domain - fails`() = runTest {
        forAll(Arb.invalidTransfer()) { entity ->
            // given
            mockkValidAccounts(entity)

            // when
            val res = with(mapper) { entity.toDomain(tags = emptyList()) }

            // then
            res.isLeft()
        }
    }

    @Test
    fun `maps invalid incomes or expense to domain - fails`() = runTest {
        forAll(Arb.invalidIncomeOrExpense()) { entity ->
            // given
            mockkValidAccounts(entity)

            // when
            val res = with(mapper) { entity.toDomain(tags = emptyList()) }

            // then
            res.isLeft()
        }
    }

    @Test
    fun `maps valid incomes or expense to domain - success`() = runTest {
        forAll(Arb.validIncomeOrExpense()) { entity ->
            // given
            mockkValidAccounts(entity)

            // when
            val res = with(mapper) { entity.toDomain(tags = emptyList()) }

            // then
            res.isRight()
        }
    }

    private fun mockkValidAccounts(entity: TransactionEntity) {
        coEvery {
            accountRepo.findById(AccountId(entity.accountId))
        } returns Arb.account().next()
        entity.toAccountId?.let { toAccountId ->
            coEvery {
                accountRepo.findById(AccountId(toAccountId))
            } returns Arb.account().next()
        }
    }
}