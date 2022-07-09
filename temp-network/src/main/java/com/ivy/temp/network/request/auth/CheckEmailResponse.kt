package com.ivy.wallet.io.network.request.auth

import com.google.gson.annotations.SerializedName
import com.ivy.wallet.domain.data.EmailState

data class CheckEmailResponse(
    @SerializedName("emailState")
    val emailState: EmailState,
    @SerializedName("firstName")
    val firstName: String?
)