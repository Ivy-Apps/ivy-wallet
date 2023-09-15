package com.ivy.wallet.domain.deprecated.logic

import com.ivy.core.data.model.IntervalType
import com.ivy.core.data.db.entity.TransactionType
import com.ivy.core.data.model.Account
import com.ivy.core.data.model.PlannedPaymentRule
import com.ivy.core.data.model.Transaction
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.currency.sumByDoublePlannedInBaseCurrency
import com.ivy.core.data.db.dao.AccountDao
import com.ivy.core.data.db.dao.PlannedPaymentRuleDao
import com.ivy.core.data.db.dao.SettingsDao
import com.ivy.core.data.db.dao.TransactionDao
import com.ivy.legacy.utils.ioThread
import com.ivy.legacy.utils.timeNowUTC
import javax.inject.Inject

@Deprecated("Migrate to FP Style")
class PlannedPaymentsLogic @Inject constructor(
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val transactionDao: TransactionDao,
    private val settingsDao: SettingsDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val accountDao: AccountDao,
) {
    companion object {
        private const val AVG_DAYS_IN_MONTH = 30.436875
    }

    suspend fun plannedPaymentsAmountFor(range: com.ivy.legacy.data.model.FromToTimeRange): Double {
        val baseCurrency = settingsDao.findFirst().currency
        val accounts = accountDao.findAll()

        return transactionDao.findAllDueToBetween(
            startDate = range.from(),
            endDate = range.to()
        ).sumOf {
            val amount = exchangeRatesLogic.amountBaseCurrency(
                transaction = it.toDomain(),
                baseCurrency = baseCurrency,
                accounts = accounts.map { it.toDomain() }
            )

            when (it.type) {
                TransactionType.INCOME -> amount
                TransactionType.EXPENSE -> -amount
                TransactionType.TRANSFER -> 0.0
            }
        }
    }

    suspend fun oneTime(): List<PlannedPaymentRule> {
        return plannedPaymentRuleDao.findAllByOneTime(oneTime = true).map { it.toDomain() }
    }

    suspend fun oneTimeIncome(): Double {
        return oneTime()
            .filter { it.type == TransactionType.INCOME }
            .sumByDoublePlannedInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun oneTimeExpenses(): Double {
        return oneTime()
            .filter { it.type == TransactionType.EXPENSE }
            .sumByDoublePlannedInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun recurring(): List<PlannedPaymentRule> =
        plannedPaymentRuleDao.findAllByOneTime(oneTime = false).map { it.toDomain() }

    suspend fun recurringIncome(): Double {
        return recurring()
            .filter { it.type == TransactionType.INCOME }
            .sumByDoubleRecurringForMonthInBaseCurrency()
    }

    suspend fun recurringExpenses(): Double {
        return recurring()
            .filter { it.type == TransactionType.EXPENSE }
            .sumByDoubleRecurringForMonthInBaseCurrency()
    }

    private suspend fun Iterable<PlannedPaymentRule>.sumByDoubleRecurringForMonthInBaseCurrency(): Double {
        val accounts = accountDao.findAll()
        val baseCurrency = settingsDao.findFirst().currency

        return sumOf {
            amountForMonthInBaseCurrency(
                plannedPayment = it,
                baseCurrency = baseCurrency,
                accounts = accounts.map { it.toDomain() }
            )
        }
    }

    private suspend fun amountForMonthInBaseCurrency(
        plannedPayment: PlannedPaymentRule,
        baseCurrency: String,
        accounts: List<Account>
    ): Double {
        val amountBaseCurrency = exchangeRatesLogic.amountBaseCurrency(
            plannedPayment = plannedPayment,
            baseCurrency = baseCurrency,
            accounts = accounts,
        )

        if (plannedPayment.oneTime) {
            return amountBaseCurrency
        }

        val intervalN = plannedPayment.intervalN ?: return amountBaseCurrency
        if (intervalN <= 0) {
            return amountBaseCurrency
        }

        return when (plannedPayment.intervalType) {
            IntervalType.DAY -> {
                val monthDiff = 1 / AVG_DAYS_IN_MONTH //0.03%

                (amountBaseCurrency / monthDiff) / intervalN
            }

            IntervalType.WEEK -> {
                val monthDiff = 7 / AVG_DAYS_IN_MONTH //0.22%

                (amountBaseCurrency / monthDiff) / intervalN
            }

            IntervalType.MONTH -> {
                amountBaseCurrency / intervalN
            }

            IntervalType.YEAR -> {
                amountBaseCurrency / (12 * intervalN)
            }

            null -> amountBaseCurrency
        }
    }

    suspend fun payOrGet(
        transaction: Transaction,
        syncTransaction: Boolean = true,
        skipTransaction: Boolean = false,
        onUpdateUI: suspend (paidTransaction: Transaction) -> Unit
    ) {
        if (transaction.dueDate == null || transaction.dateTime != null) return

        val paidTransaction = transaction.copy(
            dueDate = null,
            dateTime = timeNowUTC(),
            isSynced = false,
        )

        val plannedPaymentRule = ioThread {
            paidTransaction.recurringRuleId?.let {
                plannedPaymentRuleDao.findById(it)
            }
        }

        ioThread {
            if (skipTransaction)
                transactionDao.flagDeleted(paidTransaction.id)
            else
                transactionDao.save(paidTransaction.toEntity())


            if (plannedPaymentRule != null && plannedPaymentRule.oneTime) {
                //delete paid oneTime planned payment rules
                plannedPaymentRuleDao.flagDeleted(plannedPaymentRule.id)
            }
        }

        onUpdateUI(paidTransaction)
    }

    suspend fun payOrGet(
        transactions: List<Transaction>,
        syncTransaction: Boolean = true,
        skipTransaction: Boolean = false,
        onUpdateUI: suspend (paidTransactions: List<Transaction>) -> Unit
    ) {
        val paidTransactions =
            transactions.filter { (it.dueDate == null || it.dateTime != null).not() }

        if (paidTransactions.count() == 0) return

        paidTransactions.map {
            it.copy(
                dueDate = null,
                dateTime = timeNowUTC(),
                isSynced = false
            )
        }

        val plannedPaymentRules = ioThread {
            paidTransactions.map { transaction ->
                transaction.recurringRuleId?.let {
                    plannedPaymentRuleDao.findById(it)
                }
            }
        }

        ioThread {
            if (skipTransaction)
                paidTransactions.forEach { paidTransaction ->
                    transactionDao.flagDeleted(paidTransaction.id)
                }
            else
                paidTransactions.forEach { paidTransaction ->
                    transactionDao.save(paidTransaction.toEntity())
                }

            plannedPaymentRules.forEach { plannedPaymentRule ->
                if (plannedPaymentRule != null && plannedPaymentRule.oneTime) {
                    //delete paid oneTime planned payment rules
                    plannedPaymentRuleDao.flagDeleted(plannedPaymentRule.id)
                }
            }
        }

        onUpdateUI(paidTransactions)
    }
}
