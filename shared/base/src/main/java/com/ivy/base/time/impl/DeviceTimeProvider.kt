package com.ivy.base.time.impl

import com.ivy.base.time.TimeProvider
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@Suppress("UnnecessaryPassThroughClass")
class DeviceTimeProvider @Inject constructor() : TimeProvider {

    override fun getZoneId(): ZoneId = ZoneId.systemDefault()

    override fun utcNow(): Instant = Instant.now()

    override fun localNow(): LocalDateTime = LocalDateTime.now()

    override fun localDateNow(): LocalDate = localNow().toLocalDate()

    override fun localTimeNow(): LocalTime = localNow().toLocalTime()
}