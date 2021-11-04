package com.ivy.wallet.network.request.account

import com.ivy.wallet.model.entity.Account

data class UpdateAccountRequest(
    val account: Account? = null
)