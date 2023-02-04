package com.ivy.core.data

import arrow.core.NonEmptyList
import java.time.LocalDateTime
import java.util.*

data class RecurringRule(
    val id: UUID,
    val transaction: Transaction,
    val starting: LocalDateTime,
    val repeating: NonEmptyList<RepeatInterval>,
    val end: LocalDateTime?,
    val autoExecute: Boolean,
)

@JvmInline
value class RecurringRuleId(val id: UUID)