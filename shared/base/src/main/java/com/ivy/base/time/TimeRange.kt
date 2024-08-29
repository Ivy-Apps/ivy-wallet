package com.ivy.base.time

import java.time.Instant
import java.util.concurrent.TimeUnit

const val SAFE_OFFSET_DAYS = 365 * 10L

val INSTANT_MIN_SAFE: Instant
    get() = Instant.ofEpochMilli(Long.MIN_VALUE)
        .plusSeconds(TimeUnit.DAYS.toSeconds(SAFE_OFFSET_DAYS))

val INSTANT_MAX_SAFE: Instant
    get() = Instant.ofEpochMilli(Long.MAX_VALUE)
        .minusSeconds(TimeUnit.DAYS.toSeconds(SAFE_OFFSET_DAYS))