package com.ivy.core.domain.action.calculate.transaction

import com.ivy.common.time
import com.ivy.common.timeNowLocal
import com.ivy.common.toEpochSeconds
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.calculate.CalculateFlow
import com.ivy.core.domain.functions.transaction.overdue
import com.ivy.core.domain.functions.transaction.upcoming
import com.ivy.core.persistence.dao.trn.TrnLinkRecordDao
import com.ivy.core.persistence.entity.trn.TrnLinkRecordEntity
import com.ivy.data.transaction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

@OptIn(FlowPreview::class)
class GroupTrnsFlow @Inject constructor(
    private val calculateFlow: CalculateFlow,
    private val trnLinkRecordDao: TrnLinkRecordDao,
) : FlowAction<List<Transaction>, TransactionsList>() {

    override fun List<Transaction>.createFlow(): Flow<TransactionsList> =
        trnLinkRecordDao.findAll().map { links ->
            val trns = this
            val batchedTrns = processTrnBatches(
                trns = trns,
                links = links
            )
            val batchedTrnIds = batchedTrns.mapNotNull {
                when (it) {
                    is TrnListItem.DateDivider -> null
                    is TrnListItem.Transfer -> listOfNotNull(it.from.id, it.to.id, it.fee?.id)
                    is TrnListItem.Trn -> listOf(it.trn.id)
                }
            }.flatten()

            val nonBatchedTrns = trns.filter { !batchedTrnIds.contains(it.id) }
                .map { TrnListItem.Trn(it) }

            nonBatchedTrns + batchedTrns
        }.flatMapMerge { trnListItems ->
            combine(
                dueSectionFlow(
                    trnListItems = trnListItems,
                    trnFilter = ::upcoming,
                    createSection = ::UpcomingSection
                ),
                dueSectionFlow(
                    trnListItems = trnListItems,
                    trnFilter = ::overdue,
                    createSection = ::OverdueSection
                ),
                historyFlow(trnListItems = trnListItems),
            ) { upcomingSection, overdueSection, history ->
                TransactionsList(
                    upcoming = upcomingSection,
                    overdue = overdueSection,
                    history = history,
                )
            }
        }.flowOn(Dispatchers.Default)

    // region TrnBath processing
    private fun processTrnBatches(
        trns: List<Transaction>,
        links: List<TrnLinkRecordEntity>,
    ): List<TrnListItem> = links.groupBy { it.batchId }
        .mapNotNull { (batchId, links) ->
            val batchedTrnsIds = links.map { it.trnId }
            val batchedTrns = trns
                .filter { batchedTrnsIds.contains(it.id.toString()) }
                .takeIf { it.isNotEmpty() } ?: return@mapNotNull null

            TrnBatch(
                batchId = batchId,
                trns = batchedTrns
            )
        }.mapNotNull(::mapToDomain)


    private fun mapToDomain(trnBatch: TrnBatch): TrnListItem? =
        // We can add more domain types derived from batch later
        recognizeTransfer(trnBatch)

    private fun recognizeTransfer(trnBatch: TrnBatch): TrnListItem.Transfer? {
        val trns = trnBatch.trns
        if (trns.size != 2 && trns.size != 3) return null

        val from = trns.firstOrNull {
            it.type == TrnType.Expense && it.purpose == TrnPurpose.TransferFrom
        } ?: return null
        val to = trns.firstOrNull {
            it.type == TrnType.Income && it.purpose == TrnPurpose.TransferTo
        } ?: return null
        val fee = trns.firstOrNull {
            it.type == TrnType.Expense && it.purpose == TrnPurpose.Fee
        }

        return TrnListItem.Transfer(
            batchId = trnBatch.batchId,
            time = from.time,
            from = from,
            to = to,
            fee = fee,
        )
    }
    // endregion

    // region Upcoming & Overdue sections
    private fun <T> dueSectionFlow(
        trnListItems: List<TrnListItem>,
        trnFilter: (Transaction, now: LocalDateTime) -> Boolean,
        createSection: (income: Value, expense: Value, trns: List<Transaction>) -> T
    ): Flow<T> {
        val now = timeNowLocal()
        val upcomingTrns = trnListItems.mapNotNull {
            when (it) {
                is TrnListItem.Trn -> it.trn
                else -> null
            }
        }.filter {
            trnFilter(it, now)
        }

        return calculateFlow(
            CalculateFlow.Input(
                trns = upcomingTrns,
                includeTransfers = false,
            )
        ).map { upcomingStats ->
            // sort by the sooner the due date is
            val sortedTrns = upcomingTrns.sortedBy { it.time.time() }

            createSection(
                upcomingStats.income,
                upcomingStats.expense,
                sortedTrns
            )
        }.flowOn(Dispatchers.Default)
    }
    // endregion

    // region History
    private fun historyFlow(
        trnListItems: List<TrnListItem>
    ): Flow<List<TrnListItem>> {
        val actualTrns = trnListItems.filter {
            when (it) {
                is TrnListItem.DateDivider -> false
                is TrnListItem.Transfer -> it.time is TrnTime.Actual
                is TrnListItem.Trn -> it.trn.time is TrnTime.Actual
            }
        }

        return groupByDateFlow(actualTrns = actualTrns)
    }

    private fun groupByDateFlow(
        actualTrns: List<TrnListItem>,
    ): Flow<List<TrnListItem>> {
        if (actualTrns.isEmpty()) return flow { emptyList<TrnListItem>() }

        val historyDateTrnsMap = actualTrns
            .groupBy { actualDate(it) }
            .toSortedMap { date1, date2 ->
                if (date1 == null || date2 == null)
                    return@toSortedMap 0 //this case shouldn't happen
                (date2.atStartOfDay().toEpochSeconds() - date1.atStartOfDay()
                    .toEpochSeconds()).toInt()
            }
        return combine(
            historyDateTrnsMap.map { (date, trnsForDay) ->
                trnHistoryDayFlow(
                    date = date!!,
                    trnsForDay = trnsForDay,
                )
            }
        ) { trnsPerDay ->
            trnsPerDay.flatMap { it }
        }
    }

    private fun trnHistoryDayFlow(
        date: LocalDate,
        trnsForDay: List<TrnListItem>
    ): Flow<List<TrnListItem>> {
        fun trns(item: TrnListItem): List<Transaction> = when (item) {
            is TrnListItem.DateDivider -> emptyList()
            is TrnListItem.Transfer -> listOfNotNull(item.from, item.to, item.fee)
            is TrnListItem.Trn -> listOf(item.trn)
        }

        return calculateFlow(
            CalculateFlow.Input(
                trns = trnsForDay.flatMap(::trns),
                outputCurrency = null,
                includeTransfers = true,
            )
        ).map { dayStats ->
            listOf<TrnListItem>(
                TrnListItem.DateDivider(
                    date = date,
                    cashflow = dayStats.balance,
                )
            ).plus(
                // Newest transactions appear at the top
                trnsForDay.sortedByDescending(::actualDate)
            )
        }
    }

    private fun actualDate(item: TrnListItem): LocalDate? = when (item) {
        is TrnListItem.DateDivider -> null
        is TrnListItem.Transfer -> (item.time as? TrnTime.Actual)
        is TrnListItem.Trn -> (item.trn.time as? TrnTime.Actual)
    }?.run { actual.toLocalDate() }
    // endregion
}