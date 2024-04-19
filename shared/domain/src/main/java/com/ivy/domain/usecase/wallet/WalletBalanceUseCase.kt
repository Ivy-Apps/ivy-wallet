package com.ivy.domain.usecase.wallet

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.data.repository.AccountRepository
import com.ivy.domain.usecase.account.AccountBalanceUseCase
import javax.inject.Inject

class WalletBalanceUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val accountBalanceUseCase: AccountBalanceUseCase,
) {

    /**
     * Calculates the all-time balance of Ivy Wallet by summing
     * the balances of all included (not excluded) accounts.
     * The balance can be negative.
     *
     * @return empty map for zero balance
     */
    suspend fun calculate(): Map<AssetCode, NonZeroDouble> {
        TODO("Not implemented")
    }
}