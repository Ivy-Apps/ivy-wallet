package com.ivy.domain.usecase.account

import com.ivy.data.model.AccountId
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.data.repository.TransactionRepository
import javax.inject.Inject

class AccountBalanceUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountStatsUseCase: AccountStatsUseCase,
) {
    /**
     * Calculates the all-time balance for an account
     * in all assets that it have. **Note:** the balance can be negative.
     */
    suspend fun calculate(
        account: AccountId,
    ): Map<AssetCode, NonZeroDouble> {
        TODO("Not implemented")
    }
}