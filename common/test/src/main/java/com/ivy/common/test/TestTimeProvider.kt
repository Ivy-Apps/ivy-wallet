package com.ivy.common.test

import com.ivy.common.time.TimeProvider
import com.ivy.common.time.toEpochMilli
import com.ivy.common.time.toEpochSeconds
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TestTimeProvider constructor(
    private val time: LocalDateTime,
    private val zoneId: ZoneId,
) : TimeProvider {
    override fun timeNow(): LocalDateTime = time

    override fun dateNow(): LocalDate = time.toLocalDate()

    override fun zoneId(): ZoneId = zoneId
}

fun testTimeProvider(
    localDateTime: LocalDateTime = LocalDateTime.now(),
    zoneId: ZoneId = ZoneId.systemDefault()
) = TestTimeProvider(time = localDateTime, zoneId = zoneId)

fun epochSecondsNow(): Long {
    val provider = testTimeProvider()
    return provider.timeNow().toEpochSeconds(provider)
}

fun epocMillisNow(): Long {
    val provider = testTimeProvider()
    return provider.timeNow().toEpochMilli(provider)
}
