package com.ivy.wallet.domain.pure.account

import com.ivy.data.AccountOld

fun filterExcluded(accounts: List<AccountOld>): List<AccountOld> =
    accounts.filter { it.includeInBalance }

fun accountCurrency(account: AccountOld, baseCurrency: String): String =
    account.currency ?: baseCurrency