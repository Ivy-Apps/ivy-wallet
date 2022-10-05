package com.ivy.core.persistence.query

import com.ivy.common.time.beginningOfIvyTime
import com.ivy.common.time.timeNow
import com.ivy.core.persistence.query.TrnWhere.ActualBetween
import com.ivy.core.persistence.query.TrnWhere.DueBetween
import com.ivy.data.time.TimeRange
import java.time.LocalDateTime

/**
 * Everything due **[beginIvy, now]** including this moment is overdue.
 */
fun overdue(): TrnWhere = DueBetween(
    TimeRange(from = beginningOfIvyTime(), to = timeNow())
)

/**
 * Everything due **(now, endIvy]** from 1 second after this moment is upcoming.
 */
fun upcoming(to: LocalDateTime): TrnWhere = DueBetween(
    TimeRange(from = timeNow().plusSeconds(1), to = to)
)

fun trnsForPeriod(range: TimeRange): TrnWhere =
    DueBetween(range) or ActualBetween(range)