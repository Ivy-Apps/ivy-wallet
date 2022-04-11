package com.ivy.wallet.domain.data.bankintegrations

import com.google.gson.annotations.SerializedName

data class SECustomer(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("identifier")
    val identifier: String = "",

    @SerializedName("secret")
    val secret: String = ""
)