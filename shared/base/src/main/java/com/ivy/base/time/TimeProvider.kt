package com.ivy.base.time

import java.time.ZoneId

interface TimeProvider {
    fun getZoneId(): ZoneId
}
