package com.ivy.data.model.primitive

import com.ivy.data.model.AccountId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.PositiveValue
import com.ivy.data.model.TransactionId
import com.ivy.data.model.TransactionMetadata
import com.ivy.data.model.Transfer
import com.ivy.data.model.getFromAccount
import com.ivy.data.model.getFromValue
import com.ivy.data.model.getToAccount
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.Test
import java.time.Instant
import java.util.UUID

class TransactionTest {
    @Test
    fun `getFromAccount - income`() {
        // given
        val trn = Income

        // when
        val accountId = trn.getFromAccount()

        // then
        accountId shouldBe AccountId
    }

    @Test
    fun `getFromAccount - expense`() {
        // given
        val trn = Expense

        // when
        val accountId = trn.getFromAccount()

        // then
        accountId.shouldNotBeNull() shouldBe AccountId
    }

    @Test
    fun `getFromAccount - transfer`() {
        // given
        val trn = Transfer

        // when
        val accountId = trn.getFromAccount()

        // then
        accountId shouldBe AccountId
    }

    @Test
    fun `getToAccount - income`() {
        // given
        val trn = Income

        // when
        val accountId = trn.getToAccount()

        // then
        accountId shouldBe null
    }

    @Test
    fun `getToAccount - expense`() {
        // given
        val trn = Expense

        // when
        val accountId = trn.getToAccount()

        // then
        accountId shouldBe null
    }

    @Test
    fun `getToAccount - transfer`() {
        // given
        val trn = Transfer

        // when
        val accountId = trn.getToAccount()

        // then
        accountId shouldBe ToAccountId
    }

    @Test
    fun `getFromValue - income`() {
        // given
        val trn = Income

        // when
        val value = trn.getFromValue()

        // then
        value shouldBe Income.value
    }

    @Test
    fun `getFromValue - expense`() {
        // given
        val trn = Expense

        // when
        val value = trn.getFromValue()

        // then
        value shouldBe Expense.value
    }

    @Test
    fun `getFromValue - transfer`() {
        // given
        val trn = Transfer

        // when
        val value = trn.getFromValue()

        // then
        value shouldBe Transfer.fromValue
    }

    companion object {
        val AccountId = AccountId(UUID.randomUUID())
        val ToAccountId = AccountId(UUID.randomUUID())

        val Expense = Expense(
            id = TransactionId(UUID.randomUUID()),
            title = null,
            description = null,
            category = null,
            time = Instant.EPOCH,
            settled = false,
            metadata = TransactionMetadata(
                recurringRuleId = null,
                loanId = null,
                paidForDateTime = null,
                loanRecordId = null
            ),
            tags = listOf(),
            value = PositiveValue(
                amount = PositiveDouble.unsafe(1.0),
                asset = AssetCode.EUR
            ),
            account = AccountId,
        )

        val Income = Income(
            id = TransactionId(UUID.randomUUID()),
            title = null,
            description = null,
            category = null,
            time = Instant.EPOCH,
            settled = false,
            metadata = TransactionMetadata(
                recurringRuleId = null,
                loanId = null,
                paidForDateTime = null,
                loanRecordId = null
            ),
            tags = listOf(),
            value = PositiveValue(
                amount = PositiveDouble.unsafe(1.0),
                asset = AssetCode.EUR
            ),
            account = AccountId,
        )

        val Transfer = Transfer(
            id = TransactionId(UUID.randomUUID()),
            title = null,
            description = null,
            category = null,
            time = Instant.EPOCH,
            settled = false,
            metadata = TransactionMetadata(
                recurringRuleId = null,
                loanId = null,
                paidForDateTime = null,
                loanRecordId = null
            ),
            tags = listOf(),
            fromAccount = AccountId,
            fromValue = PositiveValue(
                amount = PositiveDouble.unsafe(1.0),
                asset = AssetCode.EUR
            ),
            toValue = PositiveValue(
                amount = PositiveDouble.unsafe(1.0),
                asset = AssetCode.EUR
            ),
            toAccount = ToAccountId,
        )
    }
}