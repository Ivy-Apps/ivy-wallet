package com.ivy.core.data

import arrow.core.NonEmptyList
import com.ivy.core.data.common.RepeatInterval
import com.ivy.core.data.sync.Syncable
import com.ivy.core.data.sync.UniqueId
import java.time.LocalDateTime
import java.util.*

data class RecurringRule(
    override val id: RecurringRuleId,
    val transaction: Transaction,
    val starting: LocalDateTime,
    val repeating: NonEmptyList<RepeatInterval>,
    val end: LocalDateTime?,
    val autoExecute: Boolean,
    override val lastUpdated: LocalDateTime,
    override val removed: Boolean,
) : Syncable

@JvmInline
value class RecurringRuleId(override val uuid: UUID) : UniqueId