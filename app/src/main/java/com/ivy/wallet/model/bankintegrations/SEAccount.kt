package com.ivy.wallet.model.bankintegrations

import com.google.gson.annotations.SerializedName

data class SEAccount(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("nature")
    val nature: String,

    @SerializedName("balance")
    val balance: Double,

    @SerializedName("currency_code")
    val currency_code: String,

    @SerializedName("connection_id")
    val connection_id: String,
)