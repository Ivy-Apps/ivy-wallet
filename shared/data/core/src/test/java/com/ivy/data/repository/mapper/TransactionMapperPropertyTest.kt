package com.ivy.data.repository.mapper

import com.ivy.base.model.TransactionType
import com.ivy.data.db.entity.TransactionEntity
import com.ivy.data.model.testing.accountId
import com.ivy.data.model.testing.assetCode
import com.ivy.data.model.testing.maybe
import com.ivy.data.model.testing.or
import com.ivy.data.model.testing.positiveDoubleExact
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.double
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.localDateTime
import io.kotest.property.arbitrary.negativeDouble
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.uuid
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class TransactionMapperPropertyTest {

    private lateinit var mapper: TransactionMapper

    @Before
    fun setup() {
        mapper = TransactionMapper()
    }

    @Test
    fun `maps invalid incomes and expense to domain - fails`() = runTest {
        // given
        forAll(Arb.invalidIncomeOrExpense()) { entity ->
            // when
            val res = with(mapper) {
                entity.toDomain(
                    accountAssetCode = Arb.assetCode().bind(),
                    toAccountAssetCode = null,
                    tags = emptyList()
                )
            }

            // then
            res.isLeft()
        }
    }

    @Test
    fun `maps valid incomes and expense to domain - success`() = runTest {
        // given
        forAll(Arb.validIncomeOrExpense()) { entity ->
            // when
            val res = with(mapper) {
                entity.toDomain(
                    accountAssetCode = Arb.assetCode().bind(),
                    toAccountAssetCode = null,
                    tags = emptyList()
                )
            }

            // then
            res.isRight()
        }
    }

    private fun Arb.Companion.invalidIncomeOrExpense(): Arb<TransactionEntity> = arbitrary {
        var entity = validIncomeOrExpense().bind()
        val invalidReasons = InvalidIncomeOrExpenseReason.entries.shuffled().take(
            Arb.int(1 until InvalidIncomeOrExpenseReason.entries.size).bind()
        ).toSet()

        if (InvalidIncomeOrExpenseReason.MissingTime in invalidReasons) {
            entity = entity.copy(
                dateTime = null,
                dueDate = null,
            )
        }
        if (InvalidIncomeOrExpenseReason.InfiniteAmount in invalidReasons) {
            entity = entity.copy(
                amount = Double.POSITIVE_INFINITY
            )
        }
        if (InvalidIncomeOrExpenseReason.NonPositiveAmount in invalidReasons) {
            entity = entity.copy(
                amount = Arb.or(Arb.negativeDouble(), Arb.of(0.0)).bind()
            )
        }

        entity
    }

    private fun Arb.Companion.validIncomeOrExpense(): Arb<TransactionEntity> = arbitrary {
        val isPlannedPayment = Arb.boolean().bind()

        TransactionEntity(
            accountId = Arb.uuid().bind(),
            type = Arb.enum<TransactionType>()
                .filter { it != TransactionType.TRANSFER }.bind(),
            amount = Arb.positiveDoubleExact().bind().value,
            toAccountId = Arb.maybe(Arb.accountId()).bind()?.value,
            toAmount = Arb.maybe(Arb.double()).bind(),
            title = Arb.maybe(Arb.string()).bind(),
            description = Arb.maybe(Arb.string()).bind(),
            dateTime = Arb.localDateTime().bind().takeIf {
                !isPlannedPayment || Arb.boolean().bind()
            },
            dueDate = Arb.localDateTime().bind().takeIf {
                isPlannedPayment || Arb.boolean().bind()
            },
            categoryId = Arb.maybe(Arb.uuid()).bind(),
            recurringRuleId = Arb.maybe(Arb.uuid()).bind(),
            attachmentUrl = Arb.maybe(Arb.string()).bind(),
            loanId = Arb.maybe(Arb.uuid()).bind(),
            loanRecordId = Arb.maybe(Arb.uuid()).bind(),
            isSynced = Arb.boolean().bind(),
            isDeleted = Arb.boolean().bind(),
            id = Arb.uuid().bind()
        )
    }

    enum class InvalidIncomeOrExpenseReason {
        MissingTime,
        NonPositiveAmount,
        InfiniteAmount,
    }
}