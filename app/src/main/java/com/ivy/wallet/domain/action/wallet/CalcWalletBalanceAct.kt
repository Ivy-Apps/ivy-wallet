package com.ivy.wallet.domain.action.wallet

import com.ivy.wallet.domain.action.Action
import com.ivy.wallet.domain.fp.data.WalletDAOs
import com.ivy.wallet.domain.fp.wallet.calculateWalletBalance
import java.math.BigDecimal
import javax.inject.Inject

class CalcWalletBalanceAct @Inject constructor(
    private val walletDAOs: WalletDAOs,
) : Action<String, BigDecimal>() {
    override suspend fun String.willDo(): BigDecimal = io {
        val baseCurrency = this
        calculateWalletBalance(
            walletDAOs = walletDAOs,
            baseCurrencyCode = baseCurrency
        ).value
    }
}