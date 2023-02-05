package com.ivy.core.data

import arrow.core.NonEmptyList
import com.ivy.core.data.common.Syncable
import java.time.LocalDateTime
import java.util.*

data class RecurringRule(
    val id: UUID,
    val transaction: Transaction,
    val starting: LocalDateTime,
    val repeating: NonEmptyList<RepeatInterval>,
    val end: LocalDateTime?,
    val autoExecute: Boolean,
    override val lastUpdated: LocalDateTime,
) : Syncable

@JvmInline
value class RecurringRuleId(val id: UUID)