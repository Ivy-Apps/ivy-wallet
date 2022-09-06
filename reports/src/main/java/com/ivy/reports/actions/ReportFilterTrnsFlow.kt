package com.ivy.reports.actions

import android.util.Log
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
import com.ivy.core.functions.transaction.TrnWhere.*
import com.ivy.core.ui.temp.trash.TimePeriod
import com.ivy.data.CurrencyCode
import com.ivy.data.ExchangeRates
import com.ivy.data.account.Account
import com.ivy.data.time.Period
import com.ivy.data.transaction.*
import com.ivy.reports.FilterState
import com.ivy.reports.HeaderState
import com.ivy.reports.data.PlannedPaymentTypes
import com.ivy.reports.toImmutableItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@OptIn(FlowPreview::class)
class ReportFilterTrnsFlow @Inject constructor(
    private val baseCurrencyFlow: BaseCurrencyFlow,
    private val calculateWithTransfersFlow: CalculateWithTransfersFlow,
    private val trnsFlow: TrnsFlow,
    private val exchangeRates: ExchangeRatesFlow
) : FlowAction<FilterState, HeaderState>() {

    override fun FilterState.createFlow(): Flow<HeaderState> =
        combine(transactionFlow(), statsDataFlow()) { trnsList, transactionStats ->
            val (allTrns, actualTrns) = trnsList

//            val y = this.transactionFlow().first().first.size
//            Log.d("GGGG",""+y)

            val x = HeaderState(
                balance = transactionStats.balance,
                income = transactionStats.income,
                expenses = transactionStats.expenses,

                incomeTransactionsCount = transactionStats.incomeTransactionsCount,
                expenseTransactionsCount = transactionStats.incomeTransactionsCount,

                treatTransfersAsIncExp = false,
                showTransfersAsIncExpCheckbox = false,

                transactionsOld = allTrns.toOld().toImmutableItem(),
                accountIdFilters = this.selectedAcc.data.map { a -> a.id }
                    .toImmutableItem()
            )
            Log.d("ReportsUIGGGG",""+x)
            x
        }.flowOn(Dispatchers.Default)


    private fun FilterState.transactionFlow() = combine(
        baseCurrencyFlow(),
        trnsFlow(whereClause(this)),
        exchangeRates()
    ) { baseCurrency, trnsList, rates ->
        Log.d("ReportsUIGGGG","ByTrnsList " +trnsList.size)
        filterByAmount(
            baseCurrency = baseCurrency,
            minAmt = this.minAmount,
            maxAmt = this.maxAmount,
            exchangeRates = rates,
            transList = trnsList
        )
    }.map {
        Log.d("ReportsUIGGGG","ByWords " +it.size)
        filterByWords(
            includeKeywords = this.includeKeywords.data,
            excludeKeywords = this.excludeKeywords.data,
            transactionsList = it
        )
    }.map {
        it to getActualTrns(
            selectedPlannedPayments = this.selectedPlannedPayments.data,
            allTrns = it
        )
    }


    private fun FilterState.statsDataFlow() =
        combine(baseCurrencyFlow(), transactionFlow()) { baseCurr, trnsList ->
            val (allTrns, actualTrns) = trnsList

            calculateWithTransfersFlow(
                CalculateWithTransfersFlow.Input(
                    trns = actualTrns,
                    outputCurrency = baseCurr,
                    accounts = this.selectedAcc.data
                )
            ).map { transactionStats ->
                HeaderState(
                    balance = transactionStats.balance,
                    income = transactionStats.income,
                    expenses = transactionStats.expense,

                    incomeTransactionsCount = transactionStats.incomesCount,
                    expenseTransactionsCount = transactionStats.expensesCount,

                    treatTransfersAsIncExp = false,
                    showTransfersAsIncExpCheckbox = false,

                    transactionsOld = allTrns.toOld().toImmutableItem(),
                    accountIdFilters = this.selectedAcc.data.map { a -> a.id }
                        .toImmutableItem()
                )
            }
        }.flattenConcat()


    private fun getActualTrns(
        selectedPlannedPayments: List<PlannedPaymentTypes>,
        allTrns: List<Transaction>
    ): List<Transaction> {
        return if (selectedPlannedPayments.isEmpty())
            allTrns.filter(::actual)
        else if (selectedPlannedPayments.contains(PlannedPaymentTypes.UPCOMING)
            && selectedPlannedPayments.contains(PlannedPaymentTypes.OVERDUE)
        )
            allTrns
        else {
            val timeNow = timeNowUTC()
            val type = selectedPlannedPayments.first()
            allTrns.filter {
                when (it.time) {
                    is TrnTime.Due -> {
                        when (type) {
                            PlannedPaymentTypes.UPCOMING -> upcoming(it, timeNow)
                            PlannedPaymentTypes.OVERDUE -> overdue(it, timeNow)
                        }
                    }
                    else -> true
                }
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


    private fun whereClause(filter: FilterState): TrnWhere {
        @Suppress("FunctionName")
        fun ByDate(timePeriod: TimePeriod?): TrnWhere {
            val datePeriod = timePeriod!!.toPeriodDate(1)
            return brackets(ActualBetween(datePeriod) or DueBetween(datePeriod))
        }

        @Suppress("FunctionName")
        fun ByAccount(accounts: List<Account>): TrnWhere {
            val nonEmptyList = accounts.toNonEmptyList()
            return brackets(ByAccountIn(nonEmptyList) or ByToAccountIn(nonEmptyList))
        }

        return ByTypeIn(filter.selectedTrnTypes.data.toNonEmptyList()) and
                ByDate(filter.period.data) and
                ByAccount(filter.selectedAcc.data) and
                ByCategoryIn(filter.selectedCat.data.toNonEmptyList())
    }


    private fun <T> List<T>.toNonEmptyList() = NonEmptyList.fromListUnsafe(this)
    private fun TimePeriod.toPeriodDate(startDateOfMonth: Int): Period {
        val range = toRange(startDateOfMonth)
        val from = range.from().toLocalDate().atStartOfDay()
        val to = range.to().toLocalDate().atEndOfDay()

        return Period.FromTo(from = from, to = to)
    }

    private fun List<Transaction>.toOld(): List<TransactionOld> {
        return this.map { newTrans ->

            val toOldTrans = TransactionOld(
                accountId = newTrans.account.id,
                type = TrnType.INCOME,
                amount = newTrans.value.amount.toBigDecimal(),
                title = newTrans.title,
                description = newTrans.description,
                dateTime = (newTrans.time as? TrnTime.Actual)?.actual,
                categoryId = newTrans.category?.id,
                dueDate = (newTrans.time as? TrnTime.Due)?.due,
                recurringRuleId = newTrans.metadata.recurringRuleId,
                loanId = newTrans.metadata.loanId,
                loanRecordId = newTrans.metadata.loanRecordId,
                id = newTrans.id,
            )

            when (newTrans.type) {
                TransactionType.Income -> toOldTrans.copy(type = TrnType.INCOME)
                TransactionType.Expense -> toOldTrans.copy(type = TrnType.EXPENSE)
                else -> toOldTrans.copy(
                    type = TrnType.TRANSFER,
                    toAmount = (newTrans.type as TransactionType.Transfer).toValue.amount.toBigDecimal(),
                    toAccountId = (newTrans.type as TransactionType.Transfer).toAccount.id
                )
            }
        }
    }
}