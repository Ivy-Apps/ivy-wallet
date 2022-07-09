package com.ivy.wallet.io.network.request.auth

import com.google.gson.annotations.SerializedName

data class CheckEmailRequest(
    @SerializedName("email")
    val email: String
)