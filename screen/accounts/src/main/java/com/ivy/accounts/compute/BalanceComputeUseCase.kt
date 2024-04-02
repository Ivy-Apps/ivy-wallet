package com.ivy.accounts.compute

import arrow.core.toOption
import com.ivy.base.legacy.SharedPrefs
import com.ivy.base.threading.DispatchersProvider
import com.ivy.base.time.asLocalDateTime
import com.ivy.data.model.Account
import com.ivy.data.model.Expense
import com.ivy.data.model.Income
import com.ivy.data.model.Transaction
import com.ivy.data.model.Transfer
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.repository.TransactionRepository
import com.ivy.wallet.domain.action.exchange.ExchangeAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import kotlin.math.absoluteValue

class BalanceComputeUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    //TODO: Re-work
    private val sharedPrefs: SharedPrefs,
    //TODO: Re-work
    private val exchangeAct: ExchangeAct,
    //TODO: Re-work
    private val baseCurrencyAct: BaseCurrencyAct,
    private val dispatchers: DispatchersProvider
) {

    suspend fun compute(
        computeType: ComputeTypes
    ): BalanceComputationResult {
         val transfersAsIncomeExpense : Boolean =
            sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,false)
        //TODO Fetch from Repo?
        val baseCurrency = baseCurrencyAct(Unit)
        val transactions = getTransactions(computeType)
        val scopedTimeRange = getScopedTimeRange(computeType)

        var balance = 0.0
        var incomeInScopedTimeRange = 0.0
        var expenseInScopedTimeRange = 0.0
        var transferIncomeInScopedTimeRange = 0.0
        var transferExpenseInScopedTimeRange = 0.0

        transactions.forEach {
            val transactionAmount = fetchAmountFromTransaction(it, computeType)
            if (scopedTimeRange.isTransactionWithInTimeRange(it)) {
                when (it) {
                    is Income -> incomeInScopedTimeRange += transactionAmount.absoluteValue
                    is Expense -> expenseInScopedTimeRange += transactionAmount.absoluteValue
                    is Transfer -> {
                        if (transactionAmount >= 0.0)
                            transferIncomeInScopedTimeRange += transactionAmount.absoluteValue
                        else
                            transferExpenseInScopedTimeRange += transactionAmount.absoluteValue
                    }
                }
            }

            balance += transactionAmount
        }

        if (transfersAsIncomeExpense) {
            incomeInScopedTimeRange += transferIncomeInScopedTimeRange
            expenseInScopedTimeRange += transferExpenseInScopedTimeRange
        }

        val balanceInBaseCurrency = when (computeType) {
            is ComputeTypes.FromAccount -> {
                if (computeType.account.asset.code != baseCurrency) {
                    computeBalanceInBaseCurrency(
                        balance = balance,
                        baseCurrency = baseCurrency,
                        fromAssetCode = computeType.account.asset
                    )
                } else
                    balance
            }
        }

        return BalanceComputationResult(
            incomeInScopedTimeRange = incomeInScopedTimeRange.roundTo2DecimalPoints(),
            expenseInScopedTimeRange = expenseInScopedTimeRange.roundTo2DecimalPoints(),
            transferIncomeInScopedTimeRange = transferIncomeInScopedTimeRange.roundTo2DecimalPoints(),
            transferExpenseInScopedTimeRange = transferExpenseInScopedTimeRange.roundTo2DecimalPoints(),
            balance = balance.roundTo2DecimalPoints(),
            balanceInBaseCurrency = balanceInBaseCurrency.roundTo2DecimalPoints()
        )
    }

    private fun getScopedTimeRange(
        computeTypes: ComputeTypes
    ): ScopedTimeRange {
        return when (computeTypes) {
            is ComputeTypes.FromAccount -> computeTypes.scopedTimeRange
        }
    }

    private suspend fun getTransactions(
        computeTypes: ComputeTypes
    ): List<Transaction> {
        return when (computeTypes) {
            is ComputeTypes.FromAccount -> getTransactionsForAccount(computeTypes.account)
        }
    }

    private suspend fun computeBalanceInBaseCurrency(
        balance: Double,
        baseCurrency: String,
        fromAssetCode: AssetCode
    ): Double {
        return exchangeAct(
            ExchangeAct.Input(
                data = ExchangeData(
                    baseCurrency = baseCurrency,
                    fromCurrency = fromAssetCode.code.toOption()
                ),
                amount = balance.toBigDecimal()
            )
        ).getOrNull()?.toDouble() ?: 0.0
    }

    private fun ScopedTimeRange.isTransactionWithInTimeRange(
        transaction: Transaction
    ): Boolean {
        return transaction.time.isAfter(this.from) && transaction.time.isBefore(this.to)
    }

    private fun fetchAmountFromTransaction(
        transaction: Transaction,
        computeTypes: ComputeTypes,
    ): Double {
        val amount = when (transaction) {
            is Income -> transaction.value.amount.value    // Amount is positive if its Income
            is Expense -> (transaction.value.amount.value).unaryMinus() // Amount is negative if its Expense
            is Transfer -> {
                when (computeTypes) {
                    is ComputeTypes.FromAccount -> {
                        // Transfers as Income & Expense can't be determined if the reference account is not present
                        computeTypes.account.let {
                            // If Reference Account Id matches FromAccount Id then its an Expense treated as negative
                            if (it.id == transaction.fromAccount)
                                (transaction.fromValue.amount.value).unaryMinus()
                            else
                                transaction.toValue.amount.value
                        }
                    }
                }
            }
        }

        return amount
    }

    private suspend fun getTransactionsForAccount(account: Account): List<Transaction> {
        return withContext(dispatchers.io) {
            val startDate = Instant.EPOCH.asLocalDateTime()
            val endDate = Instant.now().asLocalDateTime()

            val transactionsHavingSameAccount = async {
                transactionRepository.findAllByAccountAndBetween(
                    accountId = account.id,
                    startDate = startDate,
                    endDate = endDate
                ).asSequence()
            }

            val transactionsHavingSameTransferToAccount = async {
                transactionRepository.findAllToAccountAndBetween(
                    toAccountId = account.id,
                    startDate = startDate,
                    endDate = endDate
                ).asSequence()
            }

            transactionsHavingSameAccount.await()
                .plus(transactionsHavingSameTransferToAccount.await())
                .toList()
        }
    }

    private fun Double.roundTo2DecimalPoints() : Double = Math.round(this * 100.0) / 100.0
}