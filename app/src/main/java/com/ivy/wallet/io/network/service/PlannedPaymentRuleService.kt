package com.ivy.wallet.io.network.service

import com.ivy.wallet.io.network.request.planned.DeletePlannedPaymentRuleRequest
import com.ivy.wallet.io.network.request.planned.PlannedPaymentRulesResponse
import com.ivy.wallet.io.network.request.planned.UpdatePlannedPaymentRuleRequest
import retrofit2.http.*

interface PlannedPaymentRuleService {
    @POST("/wallet/planned-payments/update")
    suspend fun update(@Body request: UpdatePlannedPaymentRuleRequest)

    @GET("/wallet/planned-payments")
    suspend fun get(@Query("after") after: Long? = null): PlannedPaymentRulesResponse

    @HTTP(method = "DELETE", path = "/wallet/planned-payments/delete", hasBody = true)
    suspend fun delete(@Body request: DeletePlannedPaymentRuleRequest)
}