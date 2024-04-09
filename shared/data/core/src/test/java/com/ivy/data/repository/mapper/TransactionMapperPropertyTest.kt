package com.ivy.data.repository.mapper

import com.ivy.data.invalidIncomeOrExpense
import com.ivy.data.invalidTransfer
import com.ivy.data.model.AccountId
import com.ivy.data.model.testing.account
import com.ivy.data.repository.AccountRepository
import com.ivy.data.validIncomeOrExpense
import com.ivy.data.validTransfer
import io.kotest.property.Arb
import io.kotest.property.arbitrary.next
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
        mapper = TransactionMapper(
            accountRepository = accountRepo,
        )
    }

    @Test
    fun `maps valid transfer to domain - success`() = runTest {
        forAll(Arb.validTransfer()) { entity ->
            // given
            coEvery {
                accountRepo.findById(AccountId(entity.accountId))
            } returns Arb.account().next()
            entity.toAccountId?.let { toAccountId ->
                coEvery {
                    accountRepo.findById(AccountId(toAccountId))
                } returns Arb.account().next()
            }

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
            coEvery {
                accountRepo.findById(AccountId(entity.accountId))
            } returns Arb.account().next()
            entity.toAccountId?.let { toAccountId ->
                coEvery {
                    accountRepo.findById(AccountId(toAccountId))
                } returns Arb.account().next()
            }

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
            coEvery {
                accountRepo.findById(AccountId(entity.accountId))
            } returns Arb.account().next()
            entity.toAccountId?.let { toAccountId ->
                coEvery {
                    accountRepo.findById(AccountId(toAccountId))
                } returns Arb.account().next()
            }

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
            coEvery {
                accountRepo.findById(AccountId(entity.accountId))
            } returns Arb.account().next()
            entity.toAccountId?.let { toAccountId ->
                coEvery {
                    accountRepo.findById(AccountId(toAccountId))
                } returns Arb.account().next()
            }

            // when
            val res = with(mapper) { entity.toDomain(tags = emptyList()) }

            // then
            res.isRight()
        }
    }
}