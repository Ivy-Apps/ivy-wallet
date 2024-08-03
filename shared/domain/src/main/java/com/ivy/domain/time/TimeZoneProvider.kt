package com.ivy.domain.time

import java.time.ZoneId

interface TimeZoneProvider {
    fun getZoneId(): ZoneId
}
