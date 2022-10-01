package com.ivy.reports.actions

import arrow.core.NonEmptyList
import arrow.core.getOrElse
import com.ivy.common.atEndOfDay
import com.ivy.common.timeNowUTC
import com.ivy.core.action.FlowAction
import com.ivy.core.action.currency.BaseCurrencyFlow
import com.ivy.core.action.currency.exchange.ExchangeRatesFlow
import com.ivy.core.action.transaction.TrnsFlow
import com.ivy.core.functions.exchange.exchange
import com.ivy.core.functions.transaction.*
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRates
import com.ivy.data.account.Account
import com.ivy.data.category.Category
import com.ivy.data.time.Period
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TransactionType
import com.ivy.data.transaction.TrnTime
import com.ivy.reports.data.*
import com.ivy.reports.data.ReportPlannedPaymentType.OVERDUE
import com.ivy.reports.data.ReportPlannedPaymentType.UPCOMING
import com.ivy.reports.extensions.isFilterValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
class ReportFilterTrnsFlow @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val trnsFlow: TrnsFlow,
    private val exchangeRates: ExchangeRatesFlow,
) : FlowAction<ReportFilterState, List<Transaction>>() {

    override fun ReportFilterState.createFlow(): Flow<List<Transaction>> =
        filterStateFlow()
            .filter { it.isFilterValid() }
            .flatMapMerge { transactionFlow() }
            .onEmpty { emit(emptyList()) }
            .flowOn(Dispatchers.Default)

    private fun ReportFilterState.filterStateFlow() = flowOf(this)

    private fun ReportFilterState.transactionFlow() = combine(
        baseCurrencyFlow(),
        trnsFlow(whereClause(this)),
        exchangeRates()
    ) { baseCurrency, trnsList, rates ->
        filterByAmount(
            baseCurrency = baseCurrency,
            minAmt = this.minAmount,
            maxAmt = this.maxAmount,
            exchangeRates = rates,
            transList = trnsList
        )
    }.map {
        filterByWords(
            includeKeywords = this.includeKeywords,
            excludeKeywords = this.excludeKeywords,
            transactionsList = it
        )
    }.map {
        getActualTrns(
            selectedPlannedPayments = this.selectedPlannedPayments,
            allTrns = it
        )
    }

    private fun whereClause(filter: ReportFilterState): TrnWhere {
        @Suppress("FunctionName")
        fun ByDate(timePeriod: TimePeriod?): TrnWhere {
            val datePeriod = timePeriod!!.toPeriodDate(1)
            return brackets(TrnWhere.ActualBetween(datePeriod) or TrnWhere.DueBetween(datePeriod))
        }

        @Suppress("FunctionName")
        fun ByAccount(accounts: List<Account>): TrnWhere {
            val nonEmptyList = accounts.toNonEmptyList()
            return brackets(
                TrnWhere.ByAccountIn(nonEmptyList) or TrnWhere.ByToAccountIn(
                    nonEmptyList
                )
            )
        }

        return TrnWhere.ByTypeIn(filter.selectedTrnTypes.toNonEmptyList()) and
                ByDate(filter.period) and
                ByAccount(filter.selectedAccounts.toAccountList()) and
                TrnWhere.ByCategoryIn(
                    filter.selectedCategories.toCatNonEmptyList()
                )
    }

    private suspend fun filterByAmount(
        baseCurrency: CurrencyCode,
        minAmt: Double?,
        maxAmt: Double?,
        exchangeRates: ExchangeRates,
        transList: List<Transaction>
    ): List<Transaction> {
        suspend fun amountInBaseCurrency(
            amount: Double?,
            trnCurr: CurrencyCode?,
            baseCur: CurrencyCode = baseCurrency,
        ): Double {
            amount ?: return 0.0


            return exchange(
                rates = exchangeRates,
                baseCurrency = baseCur,
                from = trnCurr ?: baseCur,
                to = baseCur,
                amount = amount
            ).getOrElse { amount }
        }

        suspend fun filterTrans(
            transactionList: List<Transaction> = transList,
            filterAmount: (Double) -> Boolean
        ): List<Transaction> {
            return transactionList.filter {
                val amt =
                    amountInBaseCurrency(amount = it.value.amount, trnCurr = it.account.currency)

                val transferTrnsValue = (it.type as? TransactionType.Transfer)?.toValue

                val toAmt = amountInBaseCurrency(
                    transferTrnsValue?.amount,
                    trnCurr = transferTrnsValue?.currency
                )

                filterAmount(amt) || filterAmount(toAmt)
            }
        }

        return when {
            minAmt != null && maxAmt != null ->
                filterTrans { amt -> amt >= minAmt && amt <= maxAmt }
            minAmt != null -> filterTrans { amt -> amt >= minAmt }
            maxAmt != null -> filterTrans { amt -> amt <= maxAmt }
            else -> {
                transList
            }
        }
    }


    private fun filterByWords(
        includeKeywords: List<String>,
        excludeKeywords: List<String>,
        transactionsList: List<Transaction>
    ): List<Transaction> {
        fun List<Transaction>.filterTrans(
            keyWords: List<String>,
            include: Boolean = true
        ): List<Transaction> {
            if (keyWords.isEmpty())
                return this

            return this.filter {
                val title = it.title ?: ""
                val description = it.description ?: ""

                keyWords.forEach { k ->
                    val key = k.trim()
                    if (title.contains(key, ignoreCase = true) ||
                        description.contains(key, ignoreCase = true)
                    )
                        return@filter include
                }

                !include
            }
        }

        return transactionsList
            .filterTrans(includeKeywords)
            .filterTrans(excludeKeywords, include = false)
    }

    private fun getActualTrns(
        selectedPlannedPayments: List<ReportPlannedPaymentType>,
        allTrns: List<Transaction>
    ): List<Transaction> {
        return if (selectedPlannedPayments.isEmpty())
            allTrns.filter(::actual)
        else if (selectedPlannedPayments.contains(UPCOMING)
            && selectedPlannedPayments.contains(OVERDUE)
        )
            allTrns
        else {
            val timeNow = timeNowUTC()
            val type = selectedPlannedPayments.first()
            allTrns.filter {
                when (it.time) {
                    is TrnTime.Due -> {
                        when (type) {
                            UPCOMING -> upcoming(it, timeNow)
                            OVERDUE -> overdue(it, timeNow)
                        }
                    }
                    else -> true
                }
            }
        }
    }

    private fun ReportFilterState.hasEmptyContents() =
        this.selectedTrnTypes.isEmpty() && period == null &&
                selectedAccounts.none { it.selected } && selectedCategories.none { it.selected } &&
                minAmount == null && maxAmount == null &&
                includeKeywords.isEmpty() && excludeKeywords.isEmpty()

    private fun <T> List<T>.toNonEmptyList() = NonEmptyList.fromListUnsafe(this)
    private fun TimePeriod.toPeriodDate(startDateOfMonth: Int): Period {
        val range = toRange(startDateOfMonth)
        val from = range.from().toLocalDate().atStartOfDay()
        val to = range.to().toLocalDate().atEndOfDay()

        return Period.FromTo(from = from, to = to)
    }

    private fun List<SelectableReportsCategory>.toCatNonEmptyList(): NonEmptyList<Category?> {
        return this.filter { it.selected }.map { c ->
            when (c.selectableCategory) {
                is ReportCategoryType.Cat -> c.selectableCategory.cat
                else -> null
            }
        }.toNonEmptyList()
    }

    private fun List<SelectableAccount>.toAccountList() =
        this.filter { it.selected }.map { it.account }
}
