package com.ivy.core.ui.algorithm.trnhistory

import android.content.Context
import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toUtc
import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.period.SelectedPeriodFlow
import com.ivy.core.domain.algorithm.calc.RatesFlow
import com.ivy.core.domain.algorithm.calc.exchangeRawStats
import com.ivy.core.domain.algorithm.trnhistory.CollapsedTrnListKeysFlow
import com.ivy.core.domain.pure.format.format
import com.ivy.core.persistence.IvyWalletCoreDb
import com.ivy.core.persistence.algorithm.trnhistory.CalcHistoryTrnView
import com.ivy.core.ui.action.AccountsUiFlow
import com.ivy.core.ui.action.CategoriesUiFlow
import com.ivy.core.ui.algorithm.trnhistory.data.PeriodDataUi
import com.ivy.core.ui.algorithm.trnhistory.data.TrnListItemUi
import com.ivy.core.ui.algorithm.trnhistory.data.raw.RawDateDivider
import com.ivy.core.ui.algorithm.trnhistory.data.raw.RawDividerType
import com.ivy.core.ui.algorithm.trnhistory.data.raw.RawDueDivider
import com.ivy.core.ui.algorithm.trnhistory.data.raw.TrnListRawSectionKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.Instant
import java.time.ZoneOffset
import javax.inject.Inject


/**
 * @return Selected period's data:
 * - Income/Expense in base currency
 * - Transactions list: upcoming, overdue, history _(grouped properly)_
 */
class PeriodDataFlow @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val selectedPeriodFlow: SelectedPeriodFlow,
    private val db: IvyWalletCoreDb,
    private val timeProvider: TimeProvider,
    private val accountsUiFlow: AccountsUiFlow,
    private val categoriesUiFlow: CategoriesUiFlow,
    private val collapsedTrnListKeysFlow: CollapsedTrnListKeysFlow,
    private val ratesFlow: RatesFlow,
) : FlowAction<PeriodDataFlow.Input, PeriodDataUi>() {
    sealed interface Input {
        object All : Input
        // TODO: Add by Category, by Account, etc
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun createFlow(input: Input): Flow<PeriodDataUi> =
        selectedPeriodFlow().flatMapLatest { period ->
            when (input) {
                Input.All -> db.calcHistoryTrnDao().findAllInPeriod(
                    from = period.range.from.toUtc(timeProvider),
                    to = period.range.to.toUtc(timeProvider)
                )
            }
        }.flatMapLatest { calHistoryTrns ->
            // TODO: Investigate memory issues!
            combine(
                flowOf(calcHistoryTrnsToRawStats(calHistoryTrns)),
                rawTrnListMapFlow(calHistoryTrns)
            ) { periodRawStats, rawTrnListMap ->
                periodRawStats to rawTrnListMap
            }
        }.flatMapLatest { (periodRawStats, rawTrnListMap) ->
            combine(
                ratesFlow(),
                collapsedTrnListKeysFlow()
            ) { rates, collapsedKeys ->
                val periodStats = exchangeRawStats(periodRawStats, rates, rates.baseCurrency)

                val today = timeProvider.dateNow()
                val items = rawTrnListMap.mapNotNull { (key, _) ->
                    key to (key.id in collapsedKeys)
                }.sortedByDescending { (key, _) ->
                    when (key) {
                        is RawDueDivider -> when (key.type) {
                            RawDividerType.Upcoming -> Instant.MAX.epochSecond
                            RawDividerType.Overdue -> Instant.MAX
                                .minusSeconds(10).epochSecond
                        }
                        is RawDateDivider -> key.date.atStartOfDay()
                            .toEpochSecond(ZoneOffset.UTC)
                    }
                }.flatMap { (key, collapsed) ->
                    val divider = when (key) {
                        is RawDueDivider -> toDueDividerUi(
                            raw = key,
                            collapsed = collapsed,
                            rates = rates,
                            getString = appContext::getString,
                        )
                        is RawDateDivider -> toDateDividerUi(
                            appContext = appContext,
                            raw = key,
                            collapsed = collapsed,
                            rates = rates,
                            today = today
                        )
                    }

                    if (collapsed) listOf(divider) else listOf(divider) + rawTrnListMap[key]!!
                }

                PeriodDataUi(
                    periodIncome = format(periodStats.income, shortenFiat = true),
                    periodExpense = format(periodStats.expense, shortenFiat = true),
                    items = items
                )
            }
        }

    private fun rawTrnListMapFlow(
        calcHistoryTrns: List<CalcHistoryTrnView>,
    ): Flow<Map<TrnListRawSectionKey, List<TrnListItemUi>>> = combine(
        accountsUiFlow(),
        categoriesUiFlow()
    ) { accounts, categories ->
        if (accounts != null && categories != null) {
            rawTrnListMap(
                appContext = appContext,
                calcHistoryTrns = calcHistoryTrns,
                accounts = accounts,
                categories = categories,
                timeProvider = timeProvider
            )
        } else emptyMap()
    }
}