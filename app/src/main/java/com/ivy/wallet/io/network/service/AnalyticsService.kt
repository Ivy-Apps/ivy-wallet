package com.ivy.wallet.io.network.service

import androidx.annotation.Keep
import com.ivy.wallet.io.network.request.analytics.AnalyticsReportResponse
import com.ivy.wallet.io.network.request.analytics.LogEventRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

@Keep
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