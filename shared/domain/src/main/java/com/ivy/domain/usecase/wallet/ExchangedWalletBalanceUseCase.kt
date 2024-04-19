package com.ivy.domain.usecase.wallet

import com.ivy.data.model.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.CurrencyRepository
import com.ivy.domain.usecase.account.AccountBalanceUseCase
import javax.inject.Inject

class ExchangedWalletBalanceUseCase @Inject constructor(
    private val walletBalanceUseCase: WalletBalanceUseCase,
    private val currencyRepository: CurrencyRepository,
) {

    /**
     * Calculates the all-time balance of Ivy Wallet by summing
     * the balances of all included (not excluded) accounts.
     * The balance can be negative. Balances that can't be exchanged in [outCurrency]
     * are skipped and accumulated in [WalletBalanceResult.exchangeErrors].
     *
     * @return empty map for zero balance
     */
    suspend fun calculate(outCurrency: AssetCode): WalletBalanceResult {
        TODO("Not implemented")
    }
}

data class WalletBalanceResult(
    val value: Value,
    val exchangeErrors: Set<AssetCode>
)