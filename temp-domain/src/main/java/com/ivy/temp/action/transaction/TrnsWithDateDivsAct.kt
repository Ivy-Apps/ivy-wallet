package com.ivy.wallet.domain.action.transaction

import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.temp.persistence.ExchangeActOld
import com.ivy.temp.persistence.actInput
import com.ivy.wallet.domain.pure.transaction.transactionsWithDateDividers
import com.ivy.wallet.io.persistence.dao.AccountDao
import javax.inject.Inject

@Deprecated("use GroupTrnsByDateAct")
class TrnsWithDateDivsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val exchangeAct: ExchangeActOld
) : FPAction<TrnsWithDateDivsAct.Input, List<Any>>() {

    override suspend fun Input.compose(): suspend () -> List<Any> = suspend {
        transactionsWithDateDividers(
            transactions = transactions,
            baseCurrencyCode = baseCurrency,

            getAccount = accountDao::findById then { it?.toDomain() },
            exchange = ::actInput then exchangeAct
        )
    }

    data class Input(
        val baseCurrency: String,
        val transactions: List<TransactionOld>
    )
}