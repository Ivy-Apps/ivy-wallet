package com.ivy.wallet.domain.deprecated.logic.currency

import com.ivy.base.legacy.Transaction
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.PlannedPaymentRule
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.ExchangeRatesDao
import com.ivy.data.db.dao.read.SettingsDao
import java.util.UUID
import javax.inject.Inject

@Deprecated("Use FP style, look into `domain.fp` package")
class ExchangeRatesLogic @Inject constructor(
    private val exchangeRatesDao: ExchangeRatesDao
) {
    suspend fun amountBaseCurrency(
        plannedPayment: PlannedPaymentRule,
        baseCurrency: String,
        accounts: List<Account> // helper
    ): Double {
        return amountBaseCurrency(
            amount = plannedPayment.amount,
            accountId = plannedPayment.accountId,
            baseCurrency = baseCurrency,
            accounts = accounts
        )
    }

    suspend fun amountBaseCurrency(
        transaction: Transaction,
        baseCurrency: String,
        accounts: List<Account> // helper
    ): Double {
        return amountBaseCurrency(
            amount = transaction.amount.toDouble(),
            accountId = transaction.accountId,
            baseCurrency = baseCurrency,
            accounts = accounts
        )
    }

    suspend fun toAmountBaseCurrency(
        transaction: Transaction,
        baseCurrency: String,
        accounts: List<Account> // helper
    ): Double {
        val amount = transaction.toAmount ?: transaction.amount
        val toCurrency = accounts.find { it.id == transaction.toAccountId }?.currency
            ?: return amount.toDouble() // no conversion

        return amountBaseCurrency(
            amount = amount.toDouble(),
            amountCurrency = toCurrency,
            baseCurrency = baseCurrency
        )
    }

    private suspend fun amountBaseCurrency(
        amount: Double,
        accountId: UUID,
        baseCurrency: String,
        accounts: List<Account> // helper
    ): Double {
        val trnCurrency = accounts.find { it.id == accountId }?.currency
            ?: return amount // no conversion

        return amountBaseCurrency(
            amount = amount,
            amountCurrency = trnCurrency,
            baseCurrency = baseCurrency
        )
    }

    suspend fun amountBaseCurrency(
        amount: Double,
        amountCurrency: String,
        baseCurrency: String
    ): Double {
        return if (amountCurrency != baseCurrency) {
            // convert to base currency
            amount / exchangeRate(baseCurrency = baseCurrency, currency = amountCurrency)
        } else {
            // no conversion needed, return amount
            amount
        }
    }

    suspend fun convertAmount(
        baseCurrency: String,
        amount: Double,
        fromCurrency: String,
        toCurrency: String
    ): Double {
        if (fromCurrency == toCurrency) return amount

        val amountBaseCurrency =
            amount / exchangeRate(baseCurrency = baseCurrency, currency = fromCurrency)
        return amountBaseCurrency * exchangeRate(baseCurrency = baseCurrency, currency = toCurrency)
    }

    /**
     * base = BGN, currency = EUR => rate = 0.51
     */
    private suspend fun exchangeRate(
        baseCurrency: String,
        currency: String
    ): Double {
        val rate = exchangeRatesDao.findByBaseCurrencyAndCurrency(
            baseCurrency = baseCurrency,
            currency = currency
        )?.rate ?: return 1.0
        if (rate <= 0) {
            return 1.0
        }
        return rate
    }
}

@Deprecated("Use FP style, look into `domain.fp` package")
suspend fun Iterable<Transaction>.sumInBaseCurrency(
    exchangeRatesLogic: ExchangeRatesLogic,
    settingsDao: SettingsDao,
    accountDao: AccountDao,
): Double {
    val baseCurrency = settingsDao.findFirst().currency
    val accounts = accountDao.findAll()

    return sumOf {
        exchangeRatesLogic.amountBaseCurrency(
            transaction = it,
            baseCurrency = baseCurrency,
            accounts = accounts.map { it.toDomain() }
        )
    }
}

@Deprecated("Use FP style, look into `domain.fp` package")
suspend fun Iterable<PlannedPaymentRule>.sumByDoublePlannedInBaseCurrency(
    exchangeRatesLogic: ExchangeRatesLogic,
    settingsDao: SettingsDao,
    accountDao: AccountDao,
): Double {
    val baseCurrency = settingsDao.findFirst().currency
    val accounts = accountDao.findAll()

    return sumOf {
        exchangeRatesLogic.amountBaseCurrency(
            plannedPayment = it,
            baseCurrency = baseCurrency,
            accounts = accounts.map { it.toDomain() }
        )
    }
}
