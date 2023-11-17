package com.ivy.exchangerates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.data.db.dao.read.ExchangeRatesDao
import com.ivy.data.db.dao.write.WriteExchangeRatesDao
import com.ivy.data.db.entity.ExchangeRateEntity
import com.ivy.domain.ComposeViewModel
import com.ivy.exchangerates.data.RateUi
import com.ivy.legacy.domain.action.exchange.SyncExchangeRatesAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(
    private val exchangeRatesDao: ExchangeRatesDao,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val syncExchangeRatesAct: SyncExchangeRatesAct,
    private val exchangeRatesWriter: WriteExchangeRatesDao,
) : ComposeViewModel<RatesState, RatesEvent>() {
    private val searchQuery = MutableStateFlow("")
    private val rates = mutableStateOf<List<ExchangeRateEntity>>(persistentListOf())
    private val baseCurrency = mutableStateOf("")

    private fun toUi(entity: ExchangeRateEntity) = RateUi(
        from = entity.baseCurrency,
        to = entity.currency,
        rate = entity.rate
    )

    @Composable
    override fun uiState(): RatesState {
        LaunchedEffect(Unit) {
            onStart()
        }

        return RatesState(
            baseCurrency = baseCurrency.value,
            manual = rates.value.filter { it.manualOverride }.map(::toUi).toImmutableList(),
            automatic = rates.value.filter { !it.manualOverride }.map(::toUi).toImmutableList()
        )
    }

    private fun onStart() {
        viewModelScope.launch(Dispatchers.Default) {
            startInternally()
        }
    }

    private suspend fun startInternally() {
        baseCurrency.value = baseCurrencyAct(Unit)
        combine(
            exchangeRatesDao.findAll(),
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
            rates.filter { it.baseCurrency == baseCurrency.value }
        }.collect {
            rates.value = it
        }
    }

    // region Event Handling
    override fun onEvent(event: RatesEvent) {
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
            exchangeRatesWriter.deleteByBaseCurrencyAndCurrency(
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
                exchangeRatesWriter.save(
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
                exchangeRatesWriter.save(
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
