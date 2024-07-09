package com.ivy.data.model.testing

import arrow.core.Some
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.data.model.AccountId
import com.ivy.data.model.PositiveValue
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.util.UUID

@RunWith(TestParameterInjector::class)
class ArbTransactionTest {
    @Test
    fun `generates arb income`() = runTest {
        forAll(Arb.income()) {
            true
        }
    }

    @Test
    fun `arb income respects passed params`(
        @TestParameter settled: Boolean,
    ) = runTest {
        // given
        val transactionId = ModelFixtures.TransactionId
        val accountId = ModelFixtures.AccountId
        val categoryId = ModelFixtures.CategoryId
        val now = Instant.now()
        val amount = PositiveDouble.unsafe(42.37)
        val asset = AssetCode.USD

        // when
        checkAll(
            Arb.income(
                accountId = Some(accountId),
                categoryId = Some(categoryId),
                settled = Some(settled),
                time = Some(ArbTime.Exactly(now)),
                amount = Some(amount),
                asset = Some(asset),
                id = Some(transactionId),
            )
        ) { income ->
            // then
            income.id shouldBe transactionId
            income.account shouldBe accountId
            income.category shouldBe categoryId
            income.settled shouldBe settled
            income.time shouldBe now
            income.value shouldBe PositiveValue(amount, asset)
        }
    }

    @Test
    fun `generates arb expense`() = runTest {
        forAll(Arb.expense()) {
            true
        }
    }

    @Test
    fun `arb expense respects passed params`(
        @TestParameter settled: Boolean,
    ) = runTest {
        // given
        val transactionId = ModelFixtures.TransactionId
        val accountId = ModelFixtures.AccountId
        val categoryId = ModelFixtures.CategoryId
        val now = Instant.now()
        val amount = PositiveDouble.unsafe(42.37)
        val asset = AssetCode.USD

        // when
        checkAll(
            Arb.expense(
                accountId = Some(accountId),
                categoryId = Some(categoryId),
                settled = Some(settled),
                time = Some(ArbTime.Exactly(now)),
                amount = Some(amount),
                asset = Some(asset),
                id = Some(transactionId),
            )
        ) { expense ->
            // then
            expense.id shouldBe transactionId
            expense.account shouldBe accountId
            expense.category shouldBe categoryId
            expense.settled shouldBe settled
            expense.time shouldBe now
            expense.value shouldBe PositiveValue(amount, asset)
        }
    }

    @Test
    fun `generates arb transfer`() = runTest {
        forAll(Arb.transfer()) { transfer ->
            transfer.fromAccount != transfer.toAccount
        }
    }

    @Test
    fun `arb transfer respects passed params`(
        @TestParameter settled: Boolean,
    ) = runTest {
        // given
        val transactionId = ModelFixtures.TransactionId
        val categoryId = ModelFixtures.CategoryId
        val now = Instant.now()
        val fromAccount = AccountId(UUID.randomUUID())
        val fromAmount = PositiveDouble.unsafe(42.37)
        val fromAsset = AssetCode.USD
        val toAccount = AccountId(UUID.randomUUID())
        val toAmount = PositiveDouble.unsafe(3.14)
        val toAsset = AssetCode.EUR

        // when
        checkAll(
            Arb.transfer(
                categoryId = Some(categoryId),
                settled = Some(settled),
                time = Some(ArbTime.Exactly(now)),
                id = Some(transactionId),
                fromAccount = Some(fromAccount),
                fromAmount = Some(fromAmount),
                fromAsset = Some(fromAsset),
                toAccount = Some(toAccount),
                toAmount = Some(toAmount),
                toAsset = Some(toAsset)
            )
        ) { transfer ->
            // then
            transfer.id shouldBe transactionId
            transfer.category shouldBe categoryId
            transfer.settled shouldBe settled
            transfer.time shouldBe now
            transfer.fromAccount shouldBe fromAccount
            transfer.fromValue shouldBe PositiveValue(fromAmount, fromAsset)
            transfer.toAccount shouldBe toAccount
            transfer.toValue shouldBe PositiveValue(toAmount, toAsset)
        }
    }

    @Test
    fun `generates arb transaction`() = runTest {
        forAll(Arb.transfer()) {
            true
        }
    }
}