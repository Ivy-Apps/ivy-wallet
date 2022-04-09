package com.ivy.wallet.domain.data.bankintegrations

import com.google.gson.annotations.SerializedName

data class SEAccountConnection(
    @SerializedName("account")
    val account: SEAccount,
    @SerializedName("connection")
    val connection: SEConnection
)