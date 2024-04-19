package com.ivy.domain.usecase.account

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.model.AccountId
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.domain.model.StatSummary
import com.ivy.domain.usecase.StatSummaryBuilder
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountStatsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider
) {

    suspend fun calculate(
        account: AccountId,
        transactions: List<Transaction>
    ): AccountStats = withContext(dispatchers.default) {
        val income = StatSummaryBuilder()
        val expense = StatSummaryBuilder()
        val transfersIn = StatSummaryBuilder()
        val transfersOut = StatSummaryBuilder()

        for (trn in transactions) {
            when (trn) {
                is Expense -> if (trn.account == account) {
                    expense.process(trn.value)
                }

                is Income -> if (trn.account == account) {
                    income.process(trn.value)
                }

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
}

data class AccountStats(
    val income: StatSummary,
    val expense: StatSummary,
    val transfersIn: StatSummary,
    val transfersOut: StatSummary,
) {
    companion object {
        val Zero = AccountStats(
            income = StatSummary.Zero,
            expense = StatSummary.Zero,
            transfersIn = StatSummary.Zero,
            transfersOut = StatSummary.Zero,
        )
    }
}