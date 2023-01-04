package com.ivy.core.domain.pure.transaction

import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnTime
import java.time.LocalDateTime

fun upcoming(time: TrnTime, timeNow: LocalDateTime): Boolean =
    (time as? TrnTime.Due)?.due?.isAfter(timeNow.plusSeconds(1)) ?: false

fun overdue(time: TrnTime, timeNow: LocalDateTime): Boolean =
    (time as? TrnTime.Due)?.due?.isBefore(timeNow) ?: false

fun actual(trn: Transaction): Boolean = trn.time is TrnTime.Actual