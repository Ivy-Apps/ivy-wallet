package com.ivy.wallet.domain.deprecated.logic

import com.ivy.base.FromToTimeRange
import com.ivy.common.timeNowUTC
import com.ivy.data.AccountOld
import com.ivy.data.planned.IntervalType
import com.ivy.data.planned.PlannedPaymentRule
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.currency.sumByDoublePlannedInBaseCurrency
import com.ivy.wallet.domain.deprecated.sync.uploader.PlannedPaymentRuleUploader
import com.ivy.wallet.domain.deprecated.sync.uploader.TransactionUploader
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.PlannedPaymentRuleDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.io.persistence.data.toEntity
import com.ivy.wallet.utils.ioThread

@Deprecated("Migrate to FP Style")
class PlannedPaymentsLogic(
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val transactionDao: TransactionDao,
    private val transactionUploader: TransactionUploader,
    private val settingsDao: SettingsDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val accountDao: AccountDao,
    private val plannedPaymentRuleUploader: PlannedPaymentRuleUploader
) {
    companion object {
        private const val AVG_DAYS_IN_MONTH = 30.436875
    }

    suspend fun plannedPaymentsAmountFor(range: FromToTimeRange): Double {
        val baseCurrency = settingsDao.findFirstSuspend().currency
        val accounts = accountDao.findAllSuspend()

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
                TrnTypeOld.INCOME -> amount
                TrnTypeOld.EXPENSE -> -amount
                TrnTypeOld.TRANSFER -> 0.0
            }
        }
    }

    suspend fun oneTime(): List<PlannedPaymentRule> {
        return plannedPaymentRuleDao.findAllByOneTime(oneTime = true).map { it.toDomain() }
    }

    suspend fun oneTimeIncome(): Double {
        return oneTime()
            .filter { it.type == TrnTypeOld.INCOME }
            .sumByDoublePlannedInBaseCurrency(
                exchangeRatesLogic = exchangeRatesLogic,
                settingsDao = settingsDao,
                accountDao = accountDao
            )
    }

    suspend fun oneTimeExpenses(): Double {
        return oneTime()
            .filter { it.type == TrnTypeOld.EXPENSE }
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
            .filter { it.type == TrnTypeOld.INCOME }
            .sumByDoubleRecurringForMonthInBaseCurrency()
    }

    suspend fun recurringExpenses(): Double {
        return recurring()
            .filter { it.type == TrnTypeOld.EXPENSE }
            .sumByDoubleRecurringForMonthInBaseCurrency()
    }

    private suspend fun Iterable<PlannedPaymentRule>.sumByDoubleRecurringForMonthInBaseCurrency(): Double {
        val accounts = accountDao.findAllSuspend()
        val baseCurrency = settingsDao.findFirstSuspend().currency

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
        accounts: List<AccountOld>
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
        transaction: TransactionOld,
        syncTransaction: Boolean = true,
        skipTransaction: Boolean = false,
        onUpdateUI: suspend (paidTransaction: TransactionOld) -> Unit
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

        ioThread {
            if (syncTransaction && !skipTransaction) {
                transactionUploader.sync(paidTransaction)
            }

            if (plannedPaymentRule != null && plannedPaymentRule.oneTime) {
                //delete paid oneTime planned payment rules
                plannedPaymentRuleUploader.delete(plannedPaymentRule.id)
            }
        }
    }

    suspend fun payOrGet(
        transactions: List<TransactionOld>,
        syncTransaction: Boolean = true,
        skipTransaction: Boolean = false,
        onUpdateUI: suspend (paidTransactions: List<TransactionOld>) -> Unit
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

        ioThread {
            paidTransactions.forEach { paidTransaction ->
                if (syncTransaction && !skipTransaction) {
                    transactionUploader.sync(paidTransaction)
                }
            }

            plannedPaymentRules.forEach { plannedPaymentRule ->
                if (plannedPaymentRule != null && plannedPaymentRule.oneTime) {
                    //delete paid oneTime planned payment rules
                    plannedPaymentRuleUploader.delete(plannedPaymentRule.id)
                }
            }
        }
    }
}