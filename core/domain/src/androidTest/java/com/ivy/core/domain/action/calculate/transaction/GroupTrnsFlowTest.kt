package com.ivy.core.domain.action.calculate.transaction

import com.ivy.common.test.testTimeProvider
import com.ivy.common.time.dateId
import com.ivy.core.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.core.domain.action.settings.basecurrency.WriteBaseCurrencyAct
import com.ivy.core.domain.pure.dummy.dummyTrn
import com.ivy.data.Value
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TransactionsList
import com.ivy.data.transaction.TrnListItem
import com.ivy.data.transaction.TrnTime
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class GroupTrnsFlowTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var groupTrnsFlow: GroupTrnsFlow

    @Inject
    lateinit var syncExchangeRatesAct: SyncExchangeRatesAct

    @Inject
    lateinit var writeBaseCurrencyAct: WriteBaseCurrencyAct

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun trn_history_with_2_transactions() = runBlocking {
        // Arrange
        val now = testTimeProvider().timeNow()
        val trn1 = dummyTrn(
            type = TransactionType.Expense, amount = 10.0, currency = "USD",
            time = TrnTime.Actual(now)
        )
        val trn2 = dummyTrn(
            type = TransactionType.Income, amount = 5.0, currency = "USD",
            time = TrnTime.Actual(now.minusSeconds(10))
        )
        writeBaseCurrencyAct("USD")
        syncExchangeRatesAct("USD")

        // Act
        val res = groupTrnsFlow(listOf(trn1, trn2)).take(2).last()

        // Assert
        res shouldBe TransactionsList(
            upcoming = null,
            overdue = null,
            history = listOf(
                TrnListItem.DateDivider(
                    id = testTimeProvider().dateNow().dateId(),
                    date = testTimeProvider().dateNow(),
                    cashflow = Value(amount = -5.0, currency = "USD"),
                    collapsed = false,
                ),
                TrnListItem.Trn(trn1),
                TrnListItem.Trn(trn2),
            )
        )
    }

}