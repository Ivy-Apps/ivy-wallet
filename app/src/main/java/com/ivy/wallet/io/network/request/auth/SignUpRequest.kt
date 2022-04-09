package com.ivy.wallet.io.network.request.auth

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("color")
    val color: Int = 0,
    @SerializedName("fcmToken")
    val fcmToken: String
)