package com.ivy.wallet.io.network.request.auth

import com.google.gson.annotations.SerializedName
import com.ivy.wallet.model.EmailState

data class CheckEmailResponse(
    @SerializedName("emailState")
    val emailState: EmailState,
    @SerializedName("firstName")
    val firstName: String?
)