package com.ivy.base.time.impl

import com.ivy.base.time.TimeProvider
import java.time.ZoneId
import javax.inject.Inject

class DeviceTimeProvider @Inject constructor() : TimeProvider {
    override fun getZoneId(): ZoneId {
        return ZoneId.systemDefault()
    }
}