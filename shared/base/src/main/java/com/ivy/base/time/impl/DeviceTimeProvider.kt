package com.ivy.base.time.impl

import com.ivy.base.time.TimeProvider
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class DeviceTimeProvider @Inject constructor() : TimeProvider {
    override fun getZoneId(): ZoneId {
        return ZoneId.systemDefault()
    }

    override fun utcNow(): Instant {
        return Instant.now()
    }

    override fun localNow(): LocalDateTime {
        return LocalDateTime.now()
    }
}