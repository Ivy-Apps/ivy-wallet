package com.ivy.domain.usecase.balance

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.model.AccountId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.model.common.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonNegativeInt
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.domain.model.AccountStats
import com.ivy.domain.model.Summary
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountStatsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider
) {
    suspend fun calculate(
        account: AccountId,
        transactions: List<Transaction>
    ): AccountStats = withContext(dispatchers.default) {
        val income = SummaryBuilder()
        val expense = SummaryBuilder()
        val transfersIn = SummaryBuilder()
        val transfersOut = SummaryBuilder()

        for (trn in transactions) {
            when (trn) {
                is Expense -> expense.process(trn.value)
                is Income -> income.process(trn.value)
                is Transfer -> {
                    when (account) {
                        trn.fromAccount -> transfersOut.process(trn.fromValue)
                        trn.toAccount -> transfersIn.process(trn.toValue)
                        else -> {
                            // ignore, not relevant transfer for the account
                        }
                    }
                }
            }
        }

        AccountStats(
            income = income.build(),
            expense = expense.build(),
            transfersIn = transfersIn.build(),
            transfersOut = transfersOut.build()
        )
    }

    class SummaryBuilder {
        private var count = 0
        private val values = mutableMapOf<AssetCode, PositiveDouble>()

        fun process(value: Value) {
            count++
            val asset = value.asset
            // 0 + positive OR positive + positive is always positive
            values[asset] = PositiveDouble.unsafe(
                (values[asset]?.value ?: 0.0) + value.amount.value
            )
        }

        fun build(): Summary = Summary(
            trnCount = NonNegativeInt.unsafe(count),
            values = values,
        )
    }
}