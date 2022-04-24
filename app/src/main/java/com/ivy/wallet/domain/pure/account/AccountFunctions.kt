package com.ivy.wallet.domain.pure.account

import com.ivy.wallet.domain.data.core.Account

fun filterExcluded(accounts: List<Account>): List<Account> =
    accounts.filter { it.includeInBalance }