package com.ivy.wallet.io.network.request.auth

import com.google.gson.annotations.SerializedName
import com.ivy.data.user.User

data class UpdateUserInfoResponse(
    @SerializedName("user")
    val user: User
)