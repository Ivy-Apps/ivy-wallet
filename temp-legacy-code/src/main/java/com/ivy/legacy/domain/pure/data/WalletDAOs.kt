package com.ivy.wallet.domain.pure.data

import com.ivy.persistence.db.dao.read.AccountDao
import com.ivy.persistence.db.dao.read.ExchangeRatesDao
import com.ivy.persistence.db.dao.read.TransactionDao
import javax.inject.Inject

data class WalletDAOs @Inject constructor(
    val accountDao: AccountDao,
    val transactionDao: TransactionDao,
    val exchangeRatesDao: ExchangeRatesDao
)
