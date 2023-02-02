package com.ivy.core.data

import java.time.LocalDateTime

data class RecurringRule(
    val transaction: Transaction,
    val starting: LocalDateTime,
    val repeating: List<RepeatInterval>,
    val end: LocalDateTime?,
    val autoExecute: Boolean,
)