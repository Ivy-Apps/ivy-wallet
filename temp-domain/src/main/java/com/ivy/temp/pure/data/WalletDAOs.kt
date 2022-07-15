package com.ivy.wallet.domain.pure.data

import com.ivy.exchange.ExchangeRateDao
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.TransactionDao

data class WalletDAOs(
    val accountDao: AccountDao,
    val transactionDao: TransactionDao,
    val exchangeRateDao: ExchangeRateDao
)