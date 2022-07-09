package com.ivy.wallet.io.network.request.analytics

import com.ivy.wallet.domain.data.analytics.AnalyticsEvent

data class LogEventRequest(
    val event: AnalyticsEvent
)