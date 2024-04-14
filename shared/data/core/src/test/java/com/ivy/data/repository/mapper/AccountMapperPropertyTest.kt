package com.ivy.data.repository.mapper

import com.ivy.data.invalidAccountEntity
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.testing.account
import com.ivy.data.repository.CurrencyRepository
import com.ivy.data.validAccountEntity
import io.kotest.assertions.arrow.core.shouldBeLeft
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AccountMapperPropertyTest {

    private val currencyRepository = mockk<CurrencyRepository>()

    private lateinit var mapper: AccountMapper

    @Before
    fun mapper() {
        mapper = AccountMapper(
            currencyRepository = currencyRepository,
        )
    }

    @Test
    fun `property - domain-entity isomorphism`() = runTest {
        checkAll(Arb.account()) { accOrig ->
            with(mapper) {
                // when: domain -> entity -> domain
                val entityOne = accOrig.toEntity()
                val accTwo = entityOne.toDomain().getOrNull()

                // then: the recovered domain trn must be the same
                accTwo.shouldNotBeNull() shouldBe accOrig

                // and when again: domain -> entity
                val entityTwo = accTwo.toEntity()

                // then: the recovered entity must be the same
                entityTwo shouldBe entityOne
            }
        }
    }

    @Test
    fun `maps invalid accounts - always fails`() = runTest {
        checkAll(Arb.invalidAccountEntity()) { entity ->
            // given
            coEvery { currencyRepository.getBaseCurrency() } returns AssetCode.EUR

            // when
            val res = with(mapper) { entity.toDomain() }

            // then
            res.shouldBeLeft()
        }
    }

    @Test
    fun `maps valid accounts - always succeeds`() = runTest {
        checkAll(Arb.validAccountEntity()) { entity ->
            // given
            coEvery { currencyRepository.getBaseCurrency() } returns AssetCode.EUR

            // when
            val res = with(mapper) { entity.toDomain() }

            // then
            res.shouldBeRight()
        }
    }
}