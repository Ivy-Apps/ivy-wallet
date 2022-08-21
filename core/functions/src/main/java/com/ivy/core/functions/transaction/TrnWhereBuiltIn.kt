package com.ivy.core.functions.transaction

import com.ivy.common.beginningOfIvyTime
import com.ivy.common.timeNowUTC
import com.ivy.core.functions.transaction.TrnWhere.ActualBetween
import com.ivy.core.functions.transaction.TrnWhere.DueBetween
import com.ivy.data.Period
import java.time.LocalDateTime

/**
 * Everything due **[beginIvy, now]** including this moment is overdue.
 */
fun overdue(): TrnWhere = DueBetween(
    Period.FromTo(from = beginningOfIvyTime(), to = timeNowUTC())
)

/**
 * Everything due **(now, endIvy]** from 1 second after this moment is upcoming.
 */
fun upcoming(to: LocalDateTime): TrnWhere = DueBetween(
    Period.FromTo(from = timeNowUTC().plusSeconds(1), to = to)
)

fun trnsForPeriod(period: Period): TrnWhere = DueBetween(period) or ActualBetween(period)