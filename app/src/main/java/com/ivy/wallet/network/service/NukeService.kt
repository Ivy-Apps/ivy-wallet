package com.ivy.wallet.network.service

import retrofit2.http.POST

interface NukeService {
    @POST("wallet/nuke/delete-all-user-data")
    suspend fun deleteAllUserData()
}