package com.ivy.wallet.io.network.service

import androidx.annotation.Keep
import retrofit2.http.POST

@Keep
interface NukeService {
    @POST("wallet/nuke/delete-all-user-data")
    suspend fun deleteAllUserData()
}