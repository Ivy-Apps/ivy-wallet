package com.ivy.common.test

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toEpochMilli
import com.ivy.common.time.toEpochSeconds
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class TestTimeProvider constructor(
    private val time: LocalDateTime,
    private val zoneOffsetHours: Int,
) : TimeProvider {
    override fun timeNow(): LocalDateTime = time

    override fun dateNow(): LocalDate = time.toLocalDate()

    override fun zoneId(): ZoneId =
        ZoneId.ofOffset("GMT", ZoneOffset.ofHours(zoneOffsetHours))
}

fun testTimeProvider(
    timeNow: LocalDateTime = LocalDateTime.now(),
    zoneOffsetHours: Int = 0,
) = TestTimeProvider(time = timeNow, zoneOffsetHours = zoneOffsetHours)

fun epochSecondsNow(): Long {
    val provider = testTimeProvider()
    return provider.timeNow().toEpochSeconds(provider)
}

fun epocMillisNow(): Long {
    val provider = testTimeProvider()
    return provider.timeNow().toEpochMilli(provider)
}
