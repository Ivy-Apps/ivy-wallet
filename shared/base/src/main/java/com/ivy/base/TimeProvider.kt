package com.ivy.base

import java.time.ZoneId
import javax.inject.Inject

@Suppress("UnnecessaryPassThroughClass")
class TimeProvider @Inject constructor() {
    fun getZoneId(): ZoneId = ZoneId.systemDefault()
}