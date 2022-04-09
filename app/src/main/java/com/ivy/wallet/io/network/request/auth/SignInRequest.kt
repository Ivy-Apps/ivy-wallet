package com.ivy.wallet.io.network.request.auth

import com.google.gson.annotations.SerializedName

data class SignInRequest(
    @SerializedName("email")
    private val email: String,
    @SerializedName("password")
    private val password: String,
    @SerializedName("fcmToken")
    private val fcmToken: String
)