package com.ivy.wallet.network.request.bankintegrations

import com.google.gson.annotations.SerializedName

data class BankConnectionSessionResponse(
    @SerializedName("connectUrl")
    val connectUrl: String
)