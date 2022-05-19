package com.ivy.wallet.domain.action.transaction

import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.action.exchange.actInput
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.transaction.transactionsWithDateDividers
import com.ivy.wallet.io.persistence.dao.AccountDao
import javax.inject.Inject

class TrnsWithDateDivsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val exchangeAct: ExchangeAct
) : FPAction<TrnsWithDateDivsAct.Input, List<TransactionHistoryItem>>() {

    override suspend fun Input.compose(): suspend () -> List<TransactionHistoryItem> = suspend {
        transactionsWithDateDividers(
            transactions = transactions,
            baseCurrencyCode = baseCurrency,

            getAccount = accountDao::findById then { it?.toDomain() },
            exchange = ::actInput then exchangeAct
        )
    }

    data class Input(
        val baseCurrency: String,
        val transactions: List<Transaction>
    )
}