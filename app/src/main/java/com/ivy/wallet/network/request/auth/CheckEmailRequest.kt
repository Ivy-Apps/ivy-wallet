package com.ivy.wallet.network.request.auth

import com.google.gson.annotations.SerializedName

data class CheckEmailRequest(
    @SerializedName("email")
    val email: String
)