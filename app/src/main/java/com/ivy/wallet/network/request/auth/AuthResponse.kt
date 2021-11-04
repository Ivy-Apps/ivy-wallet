package com.ivy.wallet.network.request.auth

import com.google.gson.annotations.SerializedName
import com.ivy.wallet.model.entity.User

data class AuthResponse(
    @SerializedName("user")
    val user: User,
    @SerializedName("sessionToken")
    val sessionToken: String
)