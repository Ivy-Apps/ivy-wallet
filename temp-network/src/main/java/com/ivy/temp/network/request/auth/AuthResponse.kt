package com.ivy.wallet.io.network.request.auth

import com.google.gson.annotations.SerializedName
import com.ivy.wallet.io.network.data.UserDTO

data class AuthResponse(
    @SerializedName("user")
    val user: UserDTO,
    @SerializedName("sessionToken")
    val sessionToken: String
)