package com.ivy.wallet.io.network.request.bankintegrations

import com.google.gson.annotations.SerializedName

data class BankConnectionSessionResponse(
    @SerializedName("connectUrl")
    val connectUrl: String
)