package com.ivy.wallet.domain.action.wallet

import com.ivy.wallet.domain.action.Action
import com.ivy.wallet.domain.data.entity.Account
import com.ivy.wallet.domain.fp.data.WalletDAOs
import com.ivy.wallet.io.persistence.dao.ExchangeRateDao
import java.math.BigDecimal
import javax.inject.Inject

class CalcAccountsBalanceAct @Inject constructor(
    private val walletDAOs: WalletDAOs,
    private val exchangeRateDao: ExchangeRateDao
) : Action<CalcAccountsBalanceAct.Input, BigDecimal>() {
    override suspend fun Input.willDo(): BigDecimal = io {

//        sumAccountValuesInCurrency(
//            accountTrns = ,
//            baseCurrencyCode = currency,
//            exchangeRateDao = exchangeRateDao,
//            valueFunctions = nonEmptyListOf(AccountValueFunctions::balance)
//        ).value
//        calculateWalletBalance(
//            walletDAOs = walletDAOs,
//            baseCurrencyCode = currency
//        ).value
        TODO()
    }

    data class Input(
        val accounts: List<Account>,
        val currency: String
    )
}