package com.ivy.wallet.io.network.service

import com.ivy.wallet.io.network.request.transaction.DeleteTransactionRequest
import com.ivy.wallet.io.network.request.transaction.TransactionsResponse
import com.ivy.wallet.io.network.request.transaction.UpdateTransactionRequest
import retrofit2.http.*

interface TransactionService {
    @POST("/wallet/transactions/update")
    suspend fun update(@Body request: UpdateTransactionRequest)

    @GET("/wallet/transactions")
    suspend fun get(@Query("after") after: Long? = null): TransactionsResponse

    @GET("/wallet/transactions/paginated")
    suspend fun getPaginated(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): TransactionsResponse


    @HTTP(method = "DELETE", path = "/wallet/transactions/delete", hasBody = true)
    suspend fun delete(@Body request: DeleteTransactionRequest)
}