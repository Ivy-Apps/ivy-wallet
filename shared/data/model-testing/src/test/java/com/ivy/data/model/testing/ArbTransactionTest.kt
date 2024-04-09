package com.ivy.data.model.testing

import arrow.core.Some
import com.ivy.data.model.common.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class ArbTransactionTest {
    @Test
    fun `generates arb income`() = runTest {
        forAll(Arb.income()) {
            true
        }
    }

    @Test
    fun `arb income respects passed params`() = runTest {
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
                settled = Some(true),
                time = Some(ArbTime.Exactly(now)),
                removed = Some(true),
                amount = Some(amount),
                asset = Some(asset),
                id = Some(transactionId),
            )
        ) { income ->
            income.id shouldBe transactionId
            income.account shouldBe accountId
            income.category shouldBe categoryId
            income.settled shouldBe true
            income.time shouldBe now
            income.value shouldBe Value(amount, asset)
        }
    }
}