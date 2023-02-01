package com.ivy.core.data

import java.time.LocalDateTime

data class RecurringRule(
    val transaction: Transaction,
    val autoExecute: Boolean,
    val start: LocalDateTime,
    val repeating: List<RepeatInterval>,
    val end: LocalDateTime?,
)