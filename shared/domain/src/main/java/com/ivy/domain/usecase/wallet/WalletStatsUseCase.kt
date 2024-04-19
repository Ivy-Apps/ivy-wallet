package com.ivy.domain.usecase.wallet

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.repository.TransactionRepository
import com.ivy.domain.model.StatSummary
import com.ivy.domain.model.TimeRange
import javax.inject.Inject

class WalletStatsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
) {
    /**
     * Calculates the stats for Ivy Wallet including excluded accounts.
     * It ignores transfers and focuses only on income and expenses.
     */
    suspend fun calculate(
        timeRange: TimeRange
    ): WalletStats {
        // Use the StatSummaryBuilder
        TODO("Not implemented")
    }
}

data class WalletStats(
    val income: StatSummary,
    val expense: StatSummary,
)