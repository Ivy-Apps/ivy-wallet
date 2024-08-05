package com.ivy.wallet.domain.action.transaction

import com.ivy.base.legacy.Transaction
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.base.time.TimeProvider
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.TagRepository
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.legacy.datamodel.temp.toLegacyDomain
import com.ivy.legacy.domain.pure.transaction.LegacyTrnDateDividers
import com.ivy.legacy.domain.pure.transaction.transactionsWithDateDividers
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.action.exchange.actInput
import javax.inject.Inject

class TrnsWithDateDivsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val exchangeAct: ExchangeAct,
    private val tagRepository: TagRepository,
    private val accountRepository: AccountRepository,
    private val timeProvider: TimeProvider,
) : FPAction<TrnsWithDateDivsAct.Input, List<TransactionHistoryItem>>() {

    override suspend fun Input.compose(): suspend () -> List<TransactionHistoryItem> = suspend {
        transactionsWithDateDividers(
            transactions = transactions,
            baseCurrencyCode = baseCurrency,
            getTags = { tagIds -> tagRepository.findByIds(tagIds) },
            getAccount = accountDao::findById then { it?.toLegacyDomain() },
            accountRepository = accountRepository,
            timeProvider = timeProvider,
            exchange = ::actInput then exchangeAct
        )
    }

    data class Input(
        val baseCurrency: String,
        val transactions: List<com.ivy.data.model.Transaction>
    )
}

@Deprecated("Uses legacy Transaction")
class LegacyTrnsWithDateDivsAct @Inject constructor(
    private val accountDao: AccountDao,
    private val exchangeAct: ExchangeAct
) : FPAction<LegacyTrnsWithDateDivsAct.Input, List<TransactionHistoryItem>>() {

    override suspend fun Input.compose(): suspend () -> List<TransactionHistoryItem> = suspend {
        LegacyTrnDateDividers.transactionsWithDateDividers(
            transactions = transactions,
            baseCurrencyCode = baseCurrency,

            getAccount = accountDao::findById then { it?.toLegacyDomain() },
            exchange = ::actInput then exchangeAct
        )
    }

    data class Input(
        val baseCurrency: String,
        val transactions: List<Transaction>
    )
}