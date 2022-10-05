package com.ivy.common.time.provider

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceTimeProvider @Inject constructor() : TimeProvider {
    override fun timeNow(): LocalDateTime = LocalDateTime.now()

    override fun dateNow(): LocalDate = LocalDate.now()

    override fun zoneId(): ZoneId = ZoneId.systemDefault()
}