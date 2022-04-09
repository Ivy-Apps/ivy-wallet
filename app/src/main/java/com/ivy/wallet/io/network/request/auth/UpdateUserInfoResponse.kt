package com.ivy.wallet.io.network.request.auth

import com.google.gson.annotations.SerializedName
import com.ivy.wallet.model.entity.User

data class UpdateUserInfoResponse(
    @SerializedName("user")
    val user: User
)