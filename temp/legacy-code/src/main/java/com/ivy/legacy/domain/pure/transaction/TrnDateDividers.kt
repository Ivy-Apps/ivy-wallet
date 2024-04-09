package com.ivy.legacy.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.base.time.convertToLocal
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.model.Tag
import com.ivy.data.model.Transaction
import com.ivy.data.model.primitive.TagId
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.TagsRepository
import com.ivy.data.repository.mapper.TransactionMapper
import com.ivy.frp.Pure
import com.ivy.frp.SideEffect
import com.ivy.frp.then
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.legacy.datamodel.temp.toImmutableLegacyTags
import com.ivy.legacy.utils.convertUTCtoLocal
import com.ivy.legacy.utils.toEpochSeconds
import com.ivy.wallet.domain.data.TransactionHistoryDateDivider
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import com.ivy.wallet.domain.pure.exchange.ExchangeTrnArgument
import com.ivy.wallet.domain.pure.exchange.exchangeInBaseCurrency
import com.ivy.wallet.domain.pure.transaction.LegacyFoldTransactions
import com.ivy.wallet.domain.pure.transaction.LegacyTrnFunctions
import com.ivy.wallet.domain.pure.transaction.expenses
import com.ivy.wallet.domain.pure.transaction.incomes
import com.ivy.wallet.domain.pure.transaction.sumTrns
import java.math.BigDecimal
import java.util.UUID

@Deprecated("Migrate to actions")
suspend fun List<Transaction>.withDateDividers(
    exchangeRatesLogic: ExchangeRatesLogic,
    settingsDao: SettingsDao,
    accountDao: AccountDao,
    tagsRepository: TagsRepository,
    accountRepository: AccountRepository,
): List<TransactionHistoryItem> {
    return transactionsWithDateDividers(
        transactions = this,
        baseCurrencyCode = settingsDao.findFirst().currency,
        getAccount = accountDao::findById then { it?.toDomain() },
        getTags = { tagsIds -> tagsRepository.findByIds(tagsIds) },
        accountRepository = accountRepository,
        exchange = { data, amount ->
            exchangeRatesLogic.convertAmount(
                baseCurrency = data.baseCurrency,
                fromCurrency = data.fromCurrency.orNull() ?: "",
                toCurrency = data.toCurrency,
                amount = amount.toDouble()
            ).toBigDecimal().toOption()
        }
    )
}

@Pure
suspend fun transactionsWithDateDividers(
    transactions: List<Transaction>,
    baseCurrencyCode: String,
    accountRepository: AccountRepository,

    @SideEffect
    getAccount: suspend (accountId: UUID) -> Account?,
    @SideEffect
    exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>,
    @SideEffect
    getTags: suspend (tagIds: List<TagId>) -> List<Tag> = { emptyList() },
): List<TransactionHistoryItem> {
    if (transactions.isEmpty()) return emptyList()
    val transactionsMapper = TransactionMapper(accountRepository)
    return transactions
        .groupBy { it.time.convertToLocal().toLocalDate() }
        .filterKeys { it != null }
        .toSortedMap { date1, date2 ->
            if (date1 == null || date2 == null) return@toSortedMap 0 // this case shouldn't happen
            (date2.atStartOfDay().toEpochSeconds() - date1.atStartOfDay().toEpochSeconds()).toInt()
        }
        .flatMap { (date, transactionsForDate) ->
            val arg = ExchangeTrnArgument(
                baseCurrency = baseCurrencyCode,
                getAccount = getAccount,
                exchange = exchange
            )

            // Required to be interoperable with [TransactionHistoryItem]
            val legacyTransactionsForDate = with(transactionsMapper) {
                transactionsForDate.map {
                    it.toEntity()
                        .toDomain(tags = getTags(it.tags).toImmutableLegacyTags())
                }
            }
            listOf<TransactionHistoryItem>(
                TransactionHistoryDateDivider(
                    date = date!!,
                    income = sumTrns(
                        incomes(transactionsForDate),
                        ::exchangeInBaseCurrency,
                        arg
                    ).toDouble(),
                    expenses = sumTrns(
                        expenses(transactionsForDate),
                        ::exchangeInBaseCurrency,
                        arg
                    ).toDouble()
                ),
            ).plus(legacyTransactionsForDate)
        }
}

@Deprecated("Uses legacy Transaction")
object LegacyTrnDateDividers {
    @Deprecated("Migrate to actions")
    suspend fun List<com.ivy.base.legacy.Transaction>.withDateDividers(
        exchangeRatesLogic: ExchangeRatesLogic,
        settingsDao: SettingsDao,
        accountDao: AccountDao
    ): List<TransactionHistoryItem> {
        return transactionsWithDateDividers(
            transactions = this,
            baseCurrencyCode = settingsDao.findFirst().currency,
            getAccount = accountDao::findById then { it?.toDomain() },
            exchange = { data, amount ->
                exchangeRatesLogic.convertAmount(
                    baseCurrency = data.baseCurrency,
                    fromCurrency = data.fromCurrency.orNull() ?: "",
                    toCurrency = data.toCurrency,
                    amount = amount.toDouble()
                ).toBigDecimal().toOption()
            }
        )
    }

    @Pure
    suspend fun transactionsWithDateDividers(
        transactions: List<com.ivy.base.legacy.Transaction>,
        baseCurrencyCode: String,

        @SideEffect
        getAccount: suspend (accountId: UUID) -> Account?,
        @SideEffect
        exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>
    ): List<TransactionHistoryItem> {
        if (transactions.isEmpty()) return emptyList()

        return transactions
            .groupBy { it.dateTime?.convertUTCtoLocal()?.toLocalDate() }
            .filterKeys { it != null }
            .toSortedMap { date1, date2 ->
                if (date1 == null || date2 == null) return@toSortedMap 0 // this case shouldn't happen
                (
                        date2.atStartOfDay().toEpochSeconds() - date1.atStartOfDay()
                            .toEpochSeconds()
                        ).toInt()
            }
            .flatMap { (date, transactionsForDate) ->
                val arg = ExchangeTrnArgument(
                    baseCurrency = baseCurrencyCode,
                    getAccount = getAccount,
                    exchange = exchange
                )

                listOf<TransactionHistoryItem>(
                    TransactionHistoryDateDivider(
                        date = date!!,
                        income = LegacyFoldTransactions.sumTrns(
                            LegacyTrnFunctions.incomes(transactionsForDate),
                            ::exchangeInBaseCurrency,
                            arg
                        ).toDouble(),
                        expenses = LegacyFoldTransactions.sumTrns(
                            LegacyTrnFunctions.expenses(transactionsForDate),
                            ::exchangeInBaseCurrency,
                            arg
                        ).toDouble()
                    ),
                ).plus(transactionsForDate)
            }
    }
}