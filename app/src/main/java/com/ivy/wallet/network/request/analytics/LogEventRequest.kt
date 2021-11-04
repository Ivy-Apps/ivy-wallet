package com.ivy.wallet.network.request.analytics

import com.ivy.wallet.model.analytics.AnalyticsEvent

data class LogEventRequest(
    val event: AnalyticsEvent
)