package com.ivy.wallet.domain.pure.data

import com.ivy.core.db.read.AccountDao
import com.ivy.core.db.read.ExchangeRatesDao
import com.ivy.core.db.read.TransactionDao
import javax.inject.Inject

data class WalletDAOs @Inject constructor(
    val accountDao: AccountDao,
    val transactionDao: TransactionDao,
    val exchangeRatesDao: ExchangeRatesDao
)
