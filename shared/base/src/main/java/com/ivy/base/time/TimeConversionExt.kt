package com.ivy.base.time

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@Deprecated("Use the TimeConverter interface via DI")
fun Instant.convertToLocal(): ZonedDateTime {
    return atZone(ZoneId.systemDefault())
}
