package com.ivy.wallet.network.error

import com.google.gson.annotations.SerializedName

data class RestError(
    @SerializedName("errorCode")
    val errorCode: ErrorCode,
    @SerializedName("msg")
    val msg: String?
)