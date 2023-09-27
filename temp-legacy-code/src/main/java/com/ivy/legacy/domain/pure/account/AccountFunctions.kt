package com.ivy.wallet.domain.pure.account

import com.ivy.domain.datamodel.Account

fun filterExcluded(accounts: List<Account>): List<Account> =
    accounts.filter { it.includeInBalance }

fun accountCurrency(account: Account, baseCurrency: String): String =
    account.currency ?: baseCurrency
