package com.ivy.wallet.domain.deprecated.logic.currency

import com.ivy.data.AccountOld
import com.ivy.data.planned.PlannedPaymentRule
import com.ivy.data.transaction.TransactionOld
import com.ivy.temp.persistence.ExchangeRate
import com.ivy.temp.persistence.ExchangeRateDao
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.network.service.CoinbaseService
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import java.util.*

@Deprecated("Use FP style, look into `domain.fp` package")
class ExchangeRatesLogic(
    restClient: RestClient,
    private val exchangeRateDao: ExchangeRateDao
) {
    private val coinbaseService = restClient.coinbaseService

    suspend fun sync(
        baseCurrency: String
    ) {
        try {
            if (baseCurrency.isBlank()) return

            val response = coinbaseService.getExchangeRates(
                url = CoinbaseService.exchangeRatesUrl(
                    baseCurrencyCode = baseCurrency
                )
            )

            response.data.rates.forEach { (currency, rate) ->
                exchangeRateDao.save(
                    ExchangeRate(
                        baseCurrency = baseCurrency,
                        currency = currency,
                        rate = rate
                    ).toEntity()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun amountBaseCurrency(
        plannedPayment: PlannedPaymentRule,
        baseCurrency: String,
        accounts: List<AccountOld> //helper
    ): Double {
        return amountBaseCurrency(
            amount = plannedPayment.amount,
            accountId = plannedPayment.accountId,
            baseCurrency = baseCurrency,
            accounts = accounts
        )
    }

    suspend fun amountBaseCurrency(
        transaction: TransactionOld,
        baseCurrency: String,
        accounts: List<AccountOld> //helper
    ): Double {
        return amountBaseCurrency(
            amount = transaction.amount.toDouble(),
            accountId = transaction.accountId,
            baseCurrency = baseCurrency,
            accounts = accounts
        )
    }

    suspend fun toAmountBaseCurrency(
        transaction: TransactionOld,
        baseCurrency: String,
        accounts: List<AccountOld> //helper
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
        accounts: List<AccountOld> //helper
    ): Double {
        val trnCurrency = accounts.find { it.id == accountId }?.currency
            ?: return amount //no conversion

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
            //convert to base currency
            amount / exchangeRate(baseCurrency = baseCurrency, currency = amountCurrency)
        } else {
            //no conversion needed, return amount
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
        val rate = exchangeRateDao.findByBaseCurrencyAndCurrency(
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
suspend fun Iterable<TransactionOld>.sumInBaseCurrency(
    exchangeRatesLogic: ExchangeRatesLogic,
    settingsDao: SettingsDao,
    accountDao: AccountDao,
): Double {
    val baseCurrency = settingsDao.findFirstSuspend().currency
    val accounts = accountDao.findAllSuspend()

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
    val baseCurrency = settingsDao.findFirstSuspend().currency
    val accounts = accountDao.findAllSuspend()

    return sumOf {
        exchangeRatesLogic.amountBaseCurrency(
            plannedPayment = it,
            baseCurrency = baseCurrency,
            accounts = accounts.map { it.toDomain() }
        )
    }
}