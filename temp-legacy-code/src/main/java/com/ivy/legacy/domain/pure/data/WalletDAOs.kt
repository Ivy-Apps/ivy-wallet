package com.ivy.wallet.domain.pure.data

import com.ivy.core.data.db.dao.AccountDao
import com.ivy.core.data.db.dao.ExchangeRateDao
import com.ivy.core.data.db.dao.TransactionDao
import javax.inject.Inject

data class WalletDAOs @Inject constructor(
    val accountDao: AccountDao,
    val transactionDao: TransactionDao,
    val exchangeRateDao: ExchangeRateDao
)
