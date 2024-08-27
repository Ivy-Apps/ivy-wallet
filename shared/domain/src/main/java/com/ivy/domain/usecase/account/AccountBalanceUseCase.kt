package com.ivy.domain.usecase.account

import arrow.core.None
import arrow.core.Option
import com.ivy.data.model.AccountId
import com.ivy.data.model.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.data.repository.TransactionRepository
import com.ivy.domain.usecase.BalanceBuilder
import com.ivy.domain.usecase.exchange.ExchangeUseCase
import javax.inject.Inject

@Suppress("UnusedPrivateProperty", "UnusedParameter")
class AccountBalanceUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountStatsUseCase: AccountStatsUseCase,
    private val exchangeUseCase: ExchangeUseCase,
) {
    /**
     * @return none balance if the balance is zero or exchange to [outCurrency]
     * failed for all assets
     */
    suspend fun calculate(
        account: AccountId,
        outCurrency: AssetCode,
    ): ExchangedAccountBalance {
        val balance = calculate(account)
        return if (balance.isEmpty()) {
            ExchangedAccountBalance.NoneBalance
        } else {
            val exchangeResult = exchangeUseCase.convert(values = balance, to = outCurrency)
            ExchangedAccountBalance(
                balance = exchangeResult.exchanged,
                exchangeErrors = exchangeResult.exchangeErrors
            )
        }
    }

    /**
     * Calculates the all-time balance for an account
     * in all assets that it have. **Note:** the balance can be negative.
     */
    suspend fun calculate(
        account: AccountId,
    ): Map<AssetCode, NonZeroDouble> {
        val accountStats = accountStatsUseCase.calculate(
            account = account,
            transactions = transactionRepository.findAll()
        )

        return BalanceBuilder().run {
            processDeposits(
                incomes = accountStats.income.values,
                transfersIn = accountStats.transfersIn.values
            )
            processWithdrawals(
                expenses = accountStats.expense.values,
                transfersOut = accountStats.transfersOut.values
            )
            build()
        }
    }
}

data class ExchangedAccountBalance(
    val balance: Option<Value>,
    val exchangeErrors: Set<AssetCode>,
) {
    companion object {
        val NoneBalance = ExchangedAccountBalance(
            balance = None,
            exchangeErrors = emptySet()
        )
    }
}