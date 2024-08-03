package com.ivy.domain.time.impl

import com.ivy.domain.time.TimeZoneProvider
import java.time.ZoneId
import javax.inject.Inject

class DeviceTimeZoneProvider @Inject constructor() : TimeZoneProvider {
    override fun getZoneId(): ZoneId {
        return ZoneId.systemDefault()
    }
}