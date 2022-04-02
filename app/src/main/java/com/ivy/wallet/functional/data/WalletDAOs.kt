package com.ivy.wallet.functional.data

import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.ExchangeRateDao
import com.ivy.wallet.persistence.dao.TransactionDao

data class WalletDAOs(
    val accountDao: AccountDao,
    val transactionDao: TransactionDao,
    val exchangeRateDao: ExchangeRateDao
)