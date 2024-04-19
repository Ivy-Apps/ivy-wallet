package com.ivy.domain.usecase.wallet

import com.ivy.data.model.NonNegativeValue
import com.ivy.data.model.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.repository.TransactionRepository
import com.ivy.domain.model.StatSummary
import javax.inject.Inject

class ExchangedWalletStatsUseCase @Inject constructor(
    private val walletStatsUseCase: WalletStatsUseCase,
) {
    /**
     * Calculates the stats for Ivy Wallet including excluded accounts.
     * It ignores transfers and focuses only on income and expenses.
     * Stats that can't be exchanged in [outCurrency] are skipped
     * and accumulated as [ExchangedWalletStats.exchangeErrors].
     */
    suspend fun calculate(outCurrency: AssetCode): ExchangedWalletStats {
        // Use the StatSummaryBuilder
        TODO("Not implemented")
    }
}

data class ExchangedWalletStats(
    val income: NonNegativeValue,
    val expense: NonNegativeValue,
    val exchangeErrors: Set<AssetCode>,
)