package com.ivy.exchangerates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import arrow.core.raise.either
import com.ivy.base.Toaster
import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.model.ExchangeRate
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.repository.CurrencyRepository
import com.ivy.data.repository.ExchangeRatesRepository
import com.ivy.domain.usecase.exchange.SyncExchangeRatesUseCase
import com.ivy.exchangerates.data.RateUi
import com.ivy.ui.ComposeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Stable
@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(
    private val syncExchangeRatesUseCase: SyncExchangeRatesUseCase,
    private val currencyRepo: CurrencyRepository,
    private val exchangeRatesRepo: ExchangeRatesRepository,
    private val dispatchers: DispatchersProvider,
    private val toaster: Toaster,
) : ComposeViewModel<RatesState, RatesEvent>() {
    private var searchQuery by mutableStateOf("")
    private var baseCurrency by mutableStateOf<AssetCode?>(null)

    private fun toUi(exchangeRate: ExchangeRate): RateUi = RateUi(
        from = exchangeRate.baseCurrency.code,
        to = exchangeRate.currency.code,
        rate = exchangeRate.rate.value
    )

    @Composable
    override fun uiState(): RatesState {
        LaunchedEffect(Unit) {
            baseCurrency = currencyRepo.getBaseCurrency().also {
                viewModelScope.launch {
                    syncExchangeRatesUseCase.sync(it)
                }
            }
        }

        val rates = getRates()

        return RatesState(
            baseCurrency = baseCurrency?.code ?: "",
            manual = rates.filter { it.manualOverride }.map(::toUi).toImmutableList(),
            automatic = rates.filter { !it.manualOverride }.map(::toUi).toImmutableList()
        )
    }

    @Composable
    private fun getRates(): List<ExchangeRate> {
        val rates by remember { exchangeRatesRepo.findAll() }
            .collectAsState(initial = emptyList())

        return rates.filter {
            if (searchQuery.isNotBlank()) {
                it.currency.code.contains(searchQuery, ignoreCase = true)
            } else {
                true
            }
        }.filter { baseCurrency == it.baseCurrency }
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
        withContext(dispatchers.io) {
            either {
                exchangeRatesRepo.deleteByBaseCurrencyAndCurrency(
                    baseCurrency = AssetCode.from(event.rate.from).bind(),
                    currency = AssetCode.from(event.rate.to).bind()
                )
            }.onRight {
                // Sync to fetch the real rate
                baseCurrency?.let { syncExchangeRatesUseCase.sync(it) }
            }.onLeft { toaster.show(it) }
        }
    }

    private fun handleSearch(event: RatesEvent.Search) {
        searchQuery = event.query.trim()
    }

    private suspend fun handleUpdateRate(event: RatesEvent.UpdateRate) {
        withContext(dispatchers.io) {
            either {
                ExchangeRate(
                    baseCurrency = AssetCode.from(event.rate.from).bind(),
                    currency = AssetCode.from(event.rate.to).bind(),
                    rate = PositiveDouble.from(event.newRate).bind(),
                    manualOverride = true
                )
            }.onRight {
                exchangeRatesRepo.save(it)
            }.onLeft { toaster.show(it) }
        }
    }

    private suspend fun handleAddRate(event: RatesEvent.AddRate) {
        withContext(dispatchers.io) {
            either {
                ExchangeRate(
                    baseCurrency = AssetCode.from(event.rate.from).bind(),
                    currency = AssetCode.from(event.rate.to).bind(),
                    rate = PositiveDouble.from(event.rate.rate).bind(),
                    manualOverride = true
                )
            }.onRight {
                exchangeRatesRepo.save(it)
            }.onLeft { toaster.show(it) }
        }
    }
    // endregion
}
