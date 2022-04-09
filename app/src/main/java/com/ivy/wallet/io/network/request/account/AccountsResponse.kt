package com.ivy.wallet.io.network.request.account

import com.ivy.wallet.model.entity.Account


data class AccountsResponse(
    val accounts: List<Account>
)