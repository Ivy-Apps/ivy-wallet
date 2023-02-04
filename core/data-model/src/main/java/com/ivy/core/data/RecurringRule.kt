package com.ivy.core.data

import arrow.core.NonEmptyList
import java.time.LocalDateTime

data class RecurringRule(
    val transaction: Transaction,
    val starting: LocalDateTime,
    val repeating: NonEmptyList<RepeatInterval>,
    val end: LocalDateTime?,
    val autoExecute: Boolean,
)