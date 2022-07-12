package com.ivy.wallet.io.network.request.auth

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("newPassword")
    val newPassword: String? = null,
    @SerializedName("otc")
    val otc: String? = null,
    @SerializedName("fcmToken")
    val fcmToken: String? = null
)