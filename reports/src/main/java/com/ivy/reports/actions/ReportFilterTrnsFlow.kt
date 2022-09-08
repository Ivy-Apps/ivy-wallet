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
import com.ivy.reports.FilterState
import com.ivy.reports.data.ReportCategoryType
import com.ivy.reports.data.ReportPlannedPaymenttType
import com.ivy.reports.data.ReportPlannedPaymenttType.OVERDUE
import com.ivy.reports.data.ReportPlannedPaymenttType.UPCOMING
import com.ivy.reports.data.SelectableAccount
import com.ivy.reports.data.SelectableReportsCategory
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
class ReportFilterTrnsFlow @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val trnsFlow: TrnsFlow,
    private val exchangeRates: ExchangeRatesFlow,
) : FlowAction<FilterState, List<Transaction>>() {

    override fun FilterState.createFlow(): Flow<List<Transaction>> =
        filterStateFlow()
            .filter { it.isValid() && !it.hasEmptyContents() }
            .flatMapMerge { transactionFlow() }
            .onEmpty { emit(emptyList()) }

    private fun FilterState.filterStateFlow() = flowOf(this)

    private fun FilterState.transactionFlow() = combine(
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
            includeKeywords = this.includeKeywords.data,
            excludeKeywords = this.excludeKeywords.data,
            transactionsList = it
        )
    }.map {
        getActualTrns(
            selectedPlannedPayments = this.selectedPlannedPayments.data,
            allTrns = it
        )
    }

    private fun whereClause(filter: FilterState): TrnWhere {
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

        return TrnWhere.ByTypeIn(filter.selectedTrnTypes.data.toNonEmptyList()) and
                ByDate(filter.period.data) and
                ByAccount(filter.selectedAcc.data.toAccountList()) and
                TrnWhere.ByCategoryIn(
                    filter.selectedCat.data.toCatList().toNonEmptyList()
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
        selectedPlannedPayments: List<ReportPlannedPaymenttType>,
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


    private fun FilterState.isValid() = when {
        selectedTrnTypes.data.isEmpty() -> false

        period.data == null -> false

        selectedAcc.data.none { it.selected } -> false

        selectedCat.data.none { it.selected } -> false

        minAmount != null && maxAmount != null -> when {
            minAmount > maxAmount -> false
            maxAmount < minAmount -> false
            else -> true
        }

        else -> true
    }

    private fun FilterState.hasEmptyContents() =
        this.selectedTrnTypes.data.isEmpty() && period.data == null &&
                selectedAcc.data.none { it.selected } && selectedCat.data.none { it.selected } &&
                minAmount == null && maxAmount == null &&
                includeKeywords.data.isEmpty() && excludeKeywords.data.isEmpty()

    private fun <T> List<T>.toNonEmptyList() = NonEmptyList.fromListUnsafe(this)
    private fun TimePeriod.toPeriodDate(startDateOfMonth: Int): Period {
        val range = toRange(startDateOfMonth)
        val from = range.from().toLocalDate().atStartOfDay()
        val to = range.to().toLocalDate().atEndOfDay()

        return Period.FromTo(from = from, to = to)
    }

    private fun List<SelectableReportsCategory>.toCatList(): List<Category?> {
        return this.filter { it.selected }.map { c ->
            when (c.selectableCategory) {
                is ReportCategoryType.Cat -> c.selectableCategory.cat
                else -> null
            }
        }
    }

    private fun List<SelectableAccount>.toAccountList() =
        this.filter { it.selected }.map { it.account }
}
