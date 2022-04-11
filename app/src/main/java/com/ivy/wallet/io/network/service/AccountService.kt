package com.ivy.wallet.io.network.service

import com.ivy.wallet.io.network.request.account.AccountsResponse
import com.ivy.wallet.io.network.request.account.DeleteAccountRequest
import com.ivy.wallet.io.network.request.account.UpdateAccountRequest
import retrofit2.http.*

interface AccountService {
    @POST("/wallet/accounts/update")
    suspend fun update(@Body request: UpdateAccountRequest)

    @GET("/wallet/accounts")
    suspend fun get(@Query("after") after: Long? = null): AccountsResponse

    @HTTP(method = "DELETE", path = "/wallet/accounts/delete", hasBody = true)
    suspend fun delete(@Body request: DeleteAccountRequest)
}