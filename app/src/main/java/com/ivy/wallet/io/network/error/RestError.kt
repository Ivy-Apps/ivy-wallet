package com.ivy.wallet.io.network.error

import com.google.gson.annotations.SerializedName

data class RestError(
    @SerializedName("errorCode")
    val errorCode: ErrorCode,
    @SerializedName("msg")
    val msg: String?
)