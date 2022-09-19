package com.ivy.core.domain.action.calculate.transaction

import com.ivy.common.time
import com.ivy.common.timeNowLocal
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.calculate.CalculateFlow
import com.ivy.core.domain.pure.calculate.transaction.batchTrns
import com.ivy.core.domain.pure.calculate.transaction.groupActualTrnsByDate
import com.ivy.core.domain.pure.transaction.overdue
import com.ivy.core.domain.pure.transaction.upcoming
import com.ivy.core.domain.pure.util.actualTrns
import com.ivy.core.domain.pure.util.extractTrns
import com.ivy.core.persistence.dao.trn.TrnLinkRecordDao
import com.ivy.data.Value
import com.ivy.data.transaction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Groups transactions into:
 * - **Upcoming:** due transactions in the future
 * - **Overdue:** due transactions in the past
 * - **History:** actual transactions grouped by date (days).
 * Each date has calculated cashflow for that date.
 *
 * @return [TransactionsList]
 *
 * _Note: all calculations are done in base currency._
 */
@OptIn(FlowPreview::class)
class GroupTrnsFlow @Inject constructor(
    private val calculateFlow: CalculateFlow,
    private val trnLinkRecordDao: TrnLinkRecordDao,
) : FlowAction<List<Transaction>, TransactionsList>() {

    override fun List<Transaction>.createFlow(): Flow<TransactionsList> =
        trnLinkRecordDao.findAll().map { links ->
            batchTrns(trns = this, links = links)
        }.flatMapMerge { batchedTrnItems ->
            combine(
                dueSectionFlow(
                    trnListItems = batchedTrnItems,
                    dueFilter = ::upcoming,
                    createSection = ::UpcomingSection
                ),
                dueSectionFlow(
                    trnListItems = batchedTrnItems,
                    dueFilter = ::overdue,
                    createSection = ::OverdueSection
                ),
                historyFlow(trnListItems = batchedTrnItems),
            ) { upcomingSection, overdueSection, history ->
                TransactionsList(
                    upcoming = upcomingSection,
                    overdue = overdueSection,
                    history = history,
                )
            }
        }.flowOn(Dispatchers.Default)


    // region Upcoming & Overdue sections
    private fun <T> dueSectionFlow(
        trnListItems: List<TrnListItem>,
        dueFilter: (Transaction, now: LocalDateTime) -> Boolean,
        createSection: (income: Value, expense: Value, trns: List<Transaction>) -> T
    ): Flow<T?> {
        val now = timeNowLocal()
        val dueTrns = trnListItems.mapNotNull {
            when (it) {
                is TrnListItem.Trn -> it.trn
                else -> null
            }
        }.filter { dueFilter(it, now) }

        // short circuit & emit null so combine doesn't get stuck
        if (dueTrns.isEmpty()) return flowOf(null)

        return calculateFlow(
            CalculateFlow.Input(
                trns = dueTrns,
                includeTransfers = false,
                includeHidden = false,
            )
        ).map { dueStats ->
            // the sooner due date, the higher in the list the transaction should appear
            // upcoming: the most near upcoming trn will appear first
            // overdue: the most overdue trn will appear first
            val sortedTrns = dueTrns.sortedBy { it.time.time() }

            createSection(
                dueStats.income,
                dueStats.expense,
                sortedTrns
            )
        }.flowOn(Dispatchers.Default)
    }
    // endregion

    // region History
    private fun historyFlow(
        trnListItems: List<TrnListItem>
    ): Flow<List<TrnListItem>> {
        val actualTrns = actualTrns(trnItems = trnListItems)
        val trnsByDay = groupActualTrnsByDate(actualTrns = actualTrns)

        // emit so the waiting for it "combine" doesn't get stuck
        if (trnsByDay.isEmpty()) return flowOf(emptyList())

        // calculate stats for each trn history day
        return combine(
            trnsByDay.map { (day, trnsForTheDay) ->
                trnHistoryDayFlow(
                    day = day,
                    trnsForTheDay = trnsForTheDay,
                )
            }
        ) { trnsPerDay ->
            trnsPerDay.flatMap { it }
        }
    }

    private fun trnHistoryDayFlow(
        day: LocalDate,
        trnsForTheDay: List<TrnListItem>
    ): Flow<List<TrnListItem>> = calculateFlow(
        CalculateFlow.Input(
            trns = trnsForTheDay.flatMap(::extractTrns),
            outputCurrency = null,
            includeTransfers = true,
            includeHidden = false,
        )
    ).map { statsForTheDay ->
        listOf(
            TrnListItem.DateDivider(
                date = day,
                cashflow = statsForTheDay.balance,
            )
        ).plus(trnsForTheDay)
    }
    // endregion
}