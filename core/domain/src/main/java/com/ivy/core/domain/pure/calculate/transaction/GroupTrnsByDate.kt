package com.ivy.core.domain.pure.calculate.transaction

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toEpochSeconds
import com.ivy.core.domain.pure.util.actualDate
import com.ivy.core.domain.pure.util.actualTime
import com.ivy.data.transaction.TrnListItem
import java.time.LocalDate

/**
 * Groups actual transactions by date and sorts them DESC
 * (the latest transactions will appear first).
 * @param actualTrns list containing only [TrnListItem.Trn] or [TrnListItem.Transfer] with
 * actual time [com.ivy.data.transaction.TrnTime.Actual].
 */
fun groupActualTrnsByDate(
    actualTrns: List<TrnListItem>,
    timeProvider: TimeProvider
): Map<LocalDate, List<TrnListItem>> {
    if (actualTrns.isEmpty()) return emptyMap()

    return actualTrns
        .groupBy { actualDate(it) }
        .filterKeys { it != null }
        .toSortedMap { date1, date2 ->
            if (date1 == null || date2 == null)
                return@toSortedMap 0 //this case shouldn't happen
            (date2.atStartOfDay().toEpochSeconds(timeProvider) - date1.atStartOfDay()
                .toEpochSeconds(timeProvider)).toInt()
        }.map { (date, trns) ->
            // Newest transactions should appear at the top
            date!! to trns.sortedByDescending(::actualTime)
        }.toMap()
}