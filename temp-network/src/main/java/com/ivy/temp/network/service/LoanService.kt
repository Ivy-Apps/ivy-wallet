package com.ivy.wallet.io.network.service

import com.ivy.wallet.io.network.request.loan.*
import retrofit2.http.*

interface LoanService {
    @POST("/wallet/loans/update")
    suspend fun update(@Body request: UpdateLoanRequest)

    @GET("/wallet/loans")
    suspend fun get(@Query("after") after: Long? = null): LoansResponse

    @HTTP(method = "DELETE", path = "/wallet/loans/delete", hasBody = true)
    suspend fun delete(@Body request: DeleteLoanRequest)

    //LOAN RECORDS ----------------------------------------------------------------
    @POST("/wallet/loans/update-record")
    suspend fun updateRecord(@Body request: UpdateLoanRecordRequest)

    @GET("/wallet/loans/records")
    suspend fun getRecords(@Query("after") after: Long? = null): LoanRecordsResponse

    @HTTP(method = "DELETE", path = "/wallet/loans/delete-record", hasBody = true)
    suspend fun deleteRecord(@Body request: DeleteLoanRecordRequest)
}