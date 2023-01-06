package com.ivy.core.domain.action.calculate.transaction

import com.ivy.common.time.dateId
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.time
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.calculate.CalculateFlow
import com.ivy.core.domain.pure.calculate.transaction.batchTrns
import com.ivy.core.domain.pure.calculate.transaction.groupActualTrnsByDate
import com.ivy.core.domain.pure.transaction.overdue
import com.ivy.core.domain.pure.transaction.upcoming
import com.ivy.core.domain.pure.util.actualTrns
import com.ivy.core.domain.pure.util.combineSafe
import com.ivy.core.domain.pure.util.extractTrns
import com.ivy.core.domain.pure.util.flattenLatest
import com.ivy.core.persistence.dao.trn.TrnLinkRecordDao
import com.ivy.data.transaction.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class GroupTrnsFlow @Inject constructor(
    private val calculateFlow: CalculateFlow,
    private val trnLinkRecordDao: TrnLinkRecordDao,
    private val timeProvider: TimeProvider,
    private val collapsedTrnsListDatesFlow: CollapsedTrnListDatesFlow,
) : FlowAction<List<Transaction>, TransactionsList>() {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun List<Transaction>.createFlow(): Flow<TransactionsList> =
        trnLinkRecordDao.findAll().map { links ->
            val visibleTrns = this.filter {
                it.state != TrnState.Hidden
            }
            batchTrns(trns = visibleTrns, links = links)
        }.flatMapLatest { batchedTrnItems ->
            combine(
                dueSectionFlow(
                    trnListItems = batchedTrnItems,
                    dueFilter = ::upcoming,
                ),
                dueSectionFlow(
                    trnListItems = batchedTrnItems,
                    dueFilter = ::overdue,
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
    private fun dueSectionFlow(
        trnListItems: List<TrnListItem>,
        dueFilter: (TrnTime, now: LocalDateTime) -> Boolean,
    ): Flow<DueSection?> {
        val now = timeProvider.timeNow()
        val dueList = trnListItems.filter {
            when (it) {
                is TrnListItem.Trn -> dueFilter(it.trn.time, now)
                is TrnListItem.Transfer -> dueFilter(it.time, now)
                is TrnListItem.DateDivider -> false
            }
        }

        // short circuit & emit null so combine doesn't get stuck
        if (dueList.isEmpty()) return flowOf(null)

        return calculateFlow(
            CalculateFlow.Input(
                // Calculate Income & Expense only for Trns + due transfer fees
                trns = dueList.mapNotNull {
                    when (it) {
                        is TrnListItem.Trn -> it.trn
                        is TrnListItem.Transfer -> it.fee
                        is TrnListItem.DateDivider -> null
                    }
                },
                includeTransfers = false,
                includeHidden = false,
            )
        ).map { dueStats ->
            // the sooner due date, the higher in the list the transaction should appear
            // upcoming: the most near upcoming trn will appear first
            // overdue: the most overdue trn will appear first
            val sortedTrns = dueList.sortedBy {
                when (it) {
                    is TrnListItem.Transfer -> it.time.time()
                    is TrnListItem.Trn -> it.trn.time.time()
                    // this should never happen because date dividers must be filtered
                    is TrnListItem.DateDivider -> timeProvider.timeNow()
                }
            }

            DueSection(
                income = dueStats.income,
                expense = dueStats.expense,
                trns = sortedTrns
            )
        }.flowOn(Dispatchers.Default)
    }
    // endregion

    // region History
    private fun historyFlow(
        trnListItems: List<TrnListItem>
    ): Flow<List<TrnListItem>> = collapsedTrnsListDatesFlow()
        .map { collapsedTrnsList ->
            val actualTrns = actualTrns(trnItems = trnListItems)
            val trnsByDay = groupActualTrnsByDate(
                actualTrns = actualTrns,
                timeProvider = timeProvider,
            )

            // calculate stats for each trn history day
            combineSafe(
                flows = trnsByDay.map { (day, trnsForTheDay) ->
                    trnHistoryDayFlow(
                        date = day,
                        trnsForTheDay = trnsForTheDay,
                        collapsed = collapsedTrnsList.contains(day.dateId()),
                    )
                },
                ifEmpty = emptyList(),
            ) {
                it.flatten()
            }
        }.flattenLatest()

    private fun trnHistoryDayFlow(
        date: LocalDate,
        trnsForTheDay: List<TrnListItem>,
        collapsed: Boolean,
    ): Flow<List<TrnListItem>> = combine(
        calculateFlow(
            CalculateFlow.Input(
                trns = trnsForTheDay.flatMap(::extractTrns),
                outputCurrency = null,
                includeTransfers = true,
                includeHidden = false,
            ),
        ),
        collapsedTrnsListDatesFlow()
    ) { statsForTheDay, collapsedDatesIds ->
        val id = date.dateId()
        val dateDivider = TrnListItem.DateDivider(
            id = id,
            date = date,
            cashflow = statsForTheDay.balance,
            collapsed = id in collapsedDatesIds
        )

        if (collapsed) listOf(dateDivider) else
            listOf(dateDivider).plus(trnsForTheDay)
    }
    // endregion
}