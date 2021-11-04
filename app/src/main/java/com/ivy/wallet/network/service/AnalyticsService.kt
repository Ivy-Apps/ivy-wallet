package com.ivy.wallet.network.service

import com.ivy.wallet.network.request.analytics.AnalyticsReportResponse
import com.ivy.wallet.network.request.analytics.LogEventRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AnalyticsService {
    @POST("/wallet/analytics/log-event")
    suspend fun logEvent(@Body request: LogEventRequest)

    @GET("/wallet/analytics/report")
    suspend fun getReport(
        @Query("startDate") startDate: Long? = null,
        @Query("endDate") endDate: Long? = null,
    ): AnalyticsReportResponse

    @POST("/wallet/analytics/nuke-test-events")
    suspend fun nukeTestEvents()
}