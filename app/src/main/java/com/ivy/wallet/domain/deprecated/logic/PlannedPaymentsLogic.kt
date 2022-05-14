package com.ivy.wallet.domain.deprecated.logic

import com.ivy.wallet.domain.data.IntervalType
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.PlannedPaymentRule
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.currency.sumByDoublePlannedInBaseCurrency
import com.ivy.wallet.domain.deprecated.sync.uploader.PlannedPaymentRuleUploader
import com.ivy.wallet.domain.deprecated.sync.uploader.TransactionUploader
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.PlannedPaymentRuleDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.ui.onboarding.model.FromToTimeRange
import com.ivy.wallet.utils.ioThread
import com.ivy.wallet.utils.timeNowUTC

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
}