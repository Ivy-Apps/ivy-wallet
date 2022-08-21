package com.ivy.core.functions.transaction

import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnTime
import java.time.LocalDateTime

fun upcoming(trn: Transaction, timeNow: LocalDateTime): Boolean =
    (trn.time as? TrnTime.Due)?.due?.isAfter(timeNow.plusSeconds(1)) ?: false

fun overdue(trn: Transaction, timeNow: LocalDateTime): Boolean =
    (trn.time as? TrnTime.Due)?.due?.isBefore(timeNow) ?: false

fun actual(trn: Transaction): Boolean = trn.time is TrnTime.Actual