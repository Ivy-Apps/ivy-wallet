package com.ivy.wallet.ui.exchangerates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.io.persistence.dao.ExchangeRateDao
import com.ivy.wallet.io.persistence.data.ExchangeRateEntity
import com.ivy.wallet.ui.exchangerates.data.RateUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(
    private val exchangeRateDao: ExchangeRateDao,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val syncExchangeRatesAct: SyncExchangeRatesAct
) : ViewModel() {
    private val searchQuery = MutableStateFlow("")

    val state = combine(
        exchangeRateDao.findAll(),
        searchQuery
    ) { rates, query ->
        if (query.isNotBlank()) {
            rates.filter {
                it.currency.contains(query, true)
            }
        } else {
            rates
        }
    }.map { rates ->
        // filter not base currency
        val baseCurrency = baseCurrencyAct(Unit)
        rates.filter { it.baseCurrency == baseCurrency }
    }.map { rates ->
        RatesState(
            baseCurrency = baseCurrencyAct(Unit),
            manual = rates.filter { it.manualOverride }.map(::toUi),
            automatic = rates.filter { !it.manualOverride }.map(::toUi)
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        RatesState(
            baseCurrency = "",
            manual = emptyList(),
            automatic = emptyList()
        )
    )

    private fun toUi(entity: ExchangeRateEntity) = RateUi(
        from = entity.baseCurrency,
        to = entity.currency,
        rate = entity.rate
    )

    // region Event Handling
    fun onEvent(event: RatesEvent) {
        viewModelScope.launch {
            when (event) {
                is RatesEvent.RemoveOverride -> handleRemoveOverride(event)
                is RatesEvent.Search -> handleSearch(event)
                is RatesEvent.UpdateRate -> handleUpdateRate(event)
                is RatesEvent.AddRate -> handleAddRate(event)
            }
        }
    }

    private suspend fun handleRemoveOverride(event: RatesEvent.RemoveOverride) {
        withContext(Dispatchers.IO) {
            exchangeRateDao.deleteByBaseCurrencyAndCurrency(
                baseCurrency = event.rate.from,
                currency = event.rate.to
            )
        }
        sync()
    }

    private fun handleSearch(event: RatesEvent.Search) {
        searchQuery.value = event.query.trim()
    }

    private suspend fun handleUpdateRate(event: RatesEvent.UpdateRate) {
        withContext(Dispatchers.IO) {
            if (event.newRate > 0.0) {
                exchangeRateDao.save(
                    ExchangeRateEntity(
                        baseCurrency = event.rate.from,
                        currency = event.rate.to,
                        rate = event.newRate,
                        manualOverride = true
                    )
                )
            }
        }
    }

    private suspend fun handleAddRate(event: RatesEvent.AddRate) {
        withContext(Dispatchers.IO) {
            if (event.rate.rate > 0.0) {
                exchangeRateDao.save(
                    ExchangeRateEntity(
                        baseCurrency = event.rate.from.uppercase().trim(),
                        currency = event.rate.to.uppercase().trim(),
                        rate = event.rate.rate,
                        manualOverride = true
                    )
                )
            }
        }
    }

    private suspend fun sync() {
        syncExchangeRatesAct(SyncExchangeRatesAct.Input(baseCurrencyAct(Unit)))
    }
    // endregion
}
