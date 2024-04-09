package com.ivy.base

import java.time.ZoneId
import javax.inject.Inject

class TimeProvider @Inject constructor() {
    fun getZoneId(): ZoneId = ZoneId.systemDefault()
}