package com.ivy.wallet.io.network.request.account

import com.ivy.wallet.domain.data.entity.Account

data class UpdateAccountRequest(
    val account: Account? = null
)