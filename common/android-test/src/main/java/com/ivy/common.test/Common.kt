package com.ivy.common.test

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

fun uuidString(): String = UUID.randomUUID().toString()

fun Instant.epochSeconds(): Instant = this.truncatedTo(ChronoUnit.SECONDS)