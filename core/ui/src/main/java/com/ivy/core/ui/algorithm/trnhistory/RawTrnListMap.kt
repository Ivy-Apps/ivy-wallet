package com.ivy.core.ui.algorithm.trnhistory

import android.content.Context
import com.ivy.common.time.dateId
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toLocal
import com.ivy.common.time.toUtc
import com.ivy.core.domain.algorithm.calc.rawStats
import com.ivy.core.domain.algorithm.trnhistory.OverdueSectionKey
import com.ivy.core.domain.algorithm.trnhistory.UpcomingSectionKey
import com.ivy.core.persistence.algorithm.trnhistory.CalcHistoryTrnView
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.core.ui.algorithm.trnhistory.data.TrnListItemUi
import com.ivy.core.ui.algorithm.trnhistory.data.raw.RawDateDivider
import com.ivy.core.ui.algorithm.trnhistory.data.raw.RawDividerType
import com.ivy.core.ui.algorithm.trnhistory.data.raw.RawDueDivider
import com.ivy.core.ui.algorithm.trnhistory.data.raw.TrnListRawSectionKey
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.data.transaction.TrnPurpose
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * What it does:
 * - groups trns into 1) date sections 2) upcoming section 3) overdue section
 * - calculates raw stats for the date, upcoming and overdue sections
 * - transforms calcHistoryTrn into TransactionUi
 * - batches calcHistoryTrns together into TransferUi
 */
fun rawTrnListMap(
    appContext: Context,
    calcHistoryTrns: List<CalcHistoryTrnView>,
    accounts: Map<String, AccountUi>,
    categories: Map<String, CategoryUi>,
    timeProvider: TimeProvider,
): Map<TrnListRawSectionKey, List<TrnListItemUi>> {
    val result = mutableMapOf<TrnListRawSectionKey, List<TrnListItemUi>>()

    val timeNow = timeProvider.timeNow()
    val timeNowInstant = timeNow.toUtc(timeProvider)

    calcHistoryTrns.groupBy {
        when (it.timeType) {
            TrnTimeType.Actual -> it.time.toLocal(timeProvider).toLocalDate()
            TrnTimeType.Due -> if (it.time > timeNowInstant)
                UpcomingSectionKey else OverdueSectionKey
        }
    }.forEach { (key, trns) ->
        val rawStats = rawStats(trns.mapNotNull {
            when (it.purpose) {
                TrnPurpose.TransferFrom, TrnPurpose.TransferTo -> null
                TrnPurpose.Fee, TrnPurpose.AdjustBalance, null -> it.toCalcTrn()
            }
        })
        val sortedTrnsUi = parseSortedTrnListItemsUi(
            appContext = appContext,
            trns = trns,
            accounts = accounts,
            categories = categories,
            timeProvider = timeProvider,
            timeNow = timeNow,
        )
        val rawKey = when (key) {
            UpcomingSectionKey -> {
                RawDueDivider(
                    id = UpcomingSectionKey,
                    type = RawDividerType.Upcoming,
                    rawStats = rawStats,
                )
            }
            OverdueSectionKey -> {
                RawDueDivider(
                    id = OverdueSectionKey,
                    type = RawDividerType.Overdue,
                    rawStats = rawStats,
                )
            }
            else -> {
                // History date
                val date = key as LocalDate
                RawDateDivider(
                    id = date.dateId(),
                    date = date,
                    cashflow = rawStats,
                )
            }
        }
        result[rawKey] = sortedTrnsUi
    }

    return result
}

fun parseSortedTrnListItemsUi(
    appContext: Context,
    trns: List<CalcHistoryTrnView>,
    accounts: Map<String, AccountUi>,
    categories: Map<String, CategoryUi>,
    timeProvider: TimeProvider,
    timeNow: LocalDateTime,
): List<TrnListItemUi> {
    return trns
        // TODO: Investigate if performance can be improved
        .groupBy { it.batchId }
        .mapNotNull { (batchId, trns) ->
            if (batchId != null) {
                // It's a single transfer!
                parseTransfer(
                    appContext = appContext,
                    batchId = batchId,
                    batch = trns,
                    accounts = accounts,
                    categories = categories,
                    timeProvider = timeProvider,
                    timeNow = timeNow,
                )?.let { listOf(it to trns.first().time) }
            } else {
                // All other transactions (batchId = null)
                trns.mapNotNull { trn ->
                    parseTransactionUi(
                        appContext = appContext,
                        trn = trn,
                        accounts = accounts,
                        categories = categories,
                        timeProvider = timeProvider,
                        timeNow = timeNow
                    )?.let { it to trn.time }
                }.takeIf { it.isNotEmpty() }
            }
        }.flatten()
        .sortedByDescending { (_, time) -> time }
        .map { it.first }
}