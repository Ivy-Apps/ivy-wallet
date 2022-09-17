package com.ivy.core.domain.pure.util

import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnListItem
import com.ivy.data.transaction.TrnTime
import java.time.LocalDate
import java.time.LocalDateTime

// Trivial, doesn't require testing
/**
 *
 */
fun actualTrns(trnItems: List<TrnListItem>): List<TrnListItem> =
    trnItems.filter {
        when (it) {
            is TrnListItem.DateDivider -> false
            is TrnListItem.Transfer -> it.time is TrnTime.Actual
            is TrnListItem.Trn -> it.trn.time is TrnTime.Actual
        }
    }

// Trivial, doesn't require testing
/**
 * Extracts a list of [Transaction] from [TrnListItem]
 * by "unbatching" [TrnListItem.Transfer] and ignoring [TrnListItem] items
 * that doesn't contain transactions.
 */
fun extractTrns(item: TrnListItem): List<Transaction> = when (item) {
    is TrnListItem.DateDivider -> emptyList()
    is TrnListItem.Transfer -> listOfNotNull(item.from, item.to, item.fee)
    is TrnListItem.Trn -> listOf(item.trn)
}

/**
 * @return the actual time of the [TrnListItem] or null
 */
fun actualTime(item: TrnListItem): LocalDateTime? = when (item) {
    is TrnListItem.DateDivider -> null
    is TrnListItem.Transfer -> (item.time as? TrnTime.Actual)
    is TrnListItem.Trn -> (item.trn.time as? TrnTime.Actual)
}?.run { actual }


/**
 * @return the actual date of the [TrnListItem] or null
 */
fun actualDate(item: TrnListItem): LocalDate? = actualTime(item)?.toLocalDate()
