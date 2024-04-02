package com.ivy.accounts.compute

import java.time.Instant

data class ScopedTimeRange(
    val from: Instant,
    val to: Instant
)
