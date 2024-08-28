package com.ivy.wallet.domain.deprecated.logic

import com.ivy.base.legacy.Transaction
import com.ivy.base.model.TransactionType
import com.ivy.base.time.TimeProvider
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.PlannedPaymentRuleDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WritePlannedPaymentRuleDao
import com.ivy.data.model.IntervalType
import com.ivy.data.model.TransactionId
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.data.temp.migration.settleNow
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.PlannedPaymentRule
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.legacy.datamodel.temp.toLegacyDomain
import com.ivy.legacy.utils.ioThread
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.deprecated.logic.currency.sumByDoublePlannedInBaseCurrency
import javax.inject.Inject

@Deprecated("Migrate to FP Style")
class PlannedPaymentsLogic @Inject constructor(
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val transactionDao: TransactionDao,
    private val settingsDao: SettingsDao,
    private val exchangeRatesLogic: ExchangeRatesLogic,
    private val accountDao: AccountDao,
    private val transactionMapper: TransactionMapper,
    private val plannedPaymentRuleWriter: WritePlannedPaymentRuleDao,
    private val transactionRepository: TransactionRepository,
    private val timeProvider: TimeProvider,
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
                transaction = it.toLegacyDomain(),
                baseCurrency = baseCurrency,
                accounts = accounts.map { it.toLegacyDomain() }
            )

            when (it.type) {
                TransactionType.INCOME -> amount
                TransactionType.EXPENSE -> -amount
                TransactionType.TRANSFER -> 0.0
            }
        }
    }

    suspend fun oneTime(): List<PlannedPaymentRule> {
        return plannedPaymentRuleDao.findAllByOneTime(oneTime = true).map { it.toLegacyDomain() }
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
        plannedPaymentRuleDao.findAllByOneTime(oneTime = false).map { it.toLegacyDomain() }

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
                accounts = accounts.map { it.toLegacyDomain() }
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
                val monthDiff = 1 / AVG_DAYS_IN_MONTH // 0.03%

                (amountBaseCurrency / monthDiff) / intervalN
            }

            IntervalType.WEEK -> {
                val monthDiff = 7 / AVG_DAYS_IN_MONTH // 0.22%

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

    @Deprecated("Uses legacy Transaction")
    suspend fun payOrGetLegacy(
        transaction: Transaction,
        syncTransaction: Boolean = true,
        skipTransaction: Boolean = false,
        onUpdateUI: suspend (paidTransaction: Transaction) -> Unit
    ) {
        if (transaction.dueDate == null || transaction.dateTime != null) return

        val paidTransaction = transaction.copy(
            paidFor = transaction.dueDate,
            dueDate = null,
            dateTime = timeProvider.utcNow(),
            isSynced = false,
        )

        val plannedPaymentRule = ioThread {
            paidTransaction.recurringRuleId?.let {
                plannedPaymentRuleDao.findById(it)
            }
        }

        ioThread {
            if (skipTransaction) {
                transactionRepository.deleteById(TransactionId(paidTransaction.id))
            } else {
                paidTransaction.toDomain(transactionMapper)?.let {
                    transactionRepository.save(it)
                }
            }

            if (plannedPaymentRule != null && plannedPaymentRule.oneTime) {
                // delete paid oneTime planned payment rules
                plannedPaymentRuleWriter.deleteById(plannedPaymentRule.id)
            }
        }

        onUpdateUI(paidTransaction)
    }

    suspend fun payOrGet(
        transaction: com.ivy.data.model.Transaction,
        syncTransaction: Boolean = true,
        skipTransaction: Boolean = false,
        onUpdateUI: suspend (paidTransaction: com.ivy.data.model.Transaction) -> Unit
    ) {
        if (transaction.settled) return

        val paidTransaction = transaction.settleNow()

        val plannedPaymentRule = ioThread {
            paidTransaction.metadata.recurringRuleId?.let {
                plannedPaymentRuleDao.findById(it)
            }
        }

        ioThread {
            if (skipTransaction) {
                transactionRepository.deleteById(paidTransaction.id)
            } else {
                transactionRepository.save(paidTransaction)
            }

            if (plannedPaymentRule != null && plannedPaymentRule.oneTime) {
                // delete paid oneTime planned payment rules
                plannedPaymentRuleWriter.deleteById(plannedPaymentRule.id)
            }
        }

        onUpdateUI(paidTransaction)
    }

    suspend fun payOrGet(
        transactions: List<com.ivy.data.model.Transaction>,
        syncTransaction: Boolean = true,
        skipTransaction: Boolean = false,
        onUpdateUI: suspend (paidTransactions: List<com.ivy.data.model.Transaction>) -> Unit
    ) {
        val paidTransactions: List<com.ivy.data.model.Transaction> =
            transactions.filter { it.settled }

        if (paidTransactions.isEmpty()) return

        paidTransactions.map {
            it.settleNow()
        }

        val plannedPaymentRules = ioThread {
            paidTransactions.map { transaction ->
                transaction.metadata.recurringRuleId?.let {
                    plannedPaymentRuleDao.findById(it)
                }
            }
        }

        ioThread {
            if (skipTransaction) {
                paidTransactions.forEach { paidTransaction ->
                    transactionRepository.deleteById(paidTransaction.id)
                }
            } else {
                paidTransactions.forEach { paidTransaction ->
                    transactionRepository.save(paidTransaction)
                }
            }

            plannedPaymentRules.forEach { plannedPaymentRule ->
                if (plannedPaymentRule != null && plannedPaymentRule.oneTime) {
                    // delete paid oneTime planned payment rules
                    plannedPaymentRuleWriter.deleteById(plannedPaymentRule.id)
                }
            }
        }

        onUpdateUI(paidTransactions)
    }

    @Deprecated("Uses legacy Transaction")
    suspend fun payOrGetLegacy(
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
                dateTime = timeProvider.utcNow(),
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
            if (skipTransaction) {
                paidTransactions.forEach { paidTransaction ->
                    transactionRepository.deleteById(TransactionId(paidTransaction.id))
                }
            } else {
                paidTransactions.forEach { paidTransaction ->
                    paidTransaction.toDomain(transactionMapper)?.let {
                        transactionRepository.save(it)
                    }
                }
            }

            plannedPaymentRules.forEach { plannedPaymentRule ->
                if (plannedPaymentRule != null && plannedPaymentRule.oneTime) {
                    // delete paid oneTime planned payment rules
                    plannedPaymentRuleWriter.deleteById(plannedPaymentRule.id)
                }
            }
        }

        onUpdateUI(paidTransactions)
    }
}