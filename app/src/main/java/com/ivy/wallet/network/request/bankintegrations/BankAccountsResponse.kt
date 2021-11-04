package com.ivy.wallet.network.request.bankintegrations

import com.google.gson.annotations.SerializedName
import com.ivy.wallet.model.bankintegrations.SEAccount

data class BankAccountsResponse(
    @SerializedName("accounts")
    val accounts: List<SEAccount>
)