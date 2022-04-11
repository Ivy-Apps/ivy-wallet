package com.ivy.wallet.io.network.service

import com.ivy.wallet.io.network.request.budget.BudgetsResponse
import com.ivy.wallet.io.network.request.budget.CrupdateBudgetRequest
import com.ivy.wallet.io.network.request.budget.DeleteBudgetRequest
import retrofit2.http.*

interface BudgetService {
    @POST("/wallet/budgets/update")
    suspend fun update(@Body request: CrupdateBudgetRequest)

    @GET("/wallet/budgets")
    suspend fun get(@Query("after") after: Long? = null): BudgetsResponse

    @HTTP(method = "DELETE", path = "/wallet/budgets/delete", hasBody = true)
    suspend fun delete(@Body request: DeleteBudgetRequest)
}