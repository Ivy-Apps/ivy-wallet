package com.ivy.wallet.io.network.request.account

import com.ivy.wallet.io.network.data.AccountDTO

data class UpdateAccountRequest(
    val account: AccountDTO? = null
)