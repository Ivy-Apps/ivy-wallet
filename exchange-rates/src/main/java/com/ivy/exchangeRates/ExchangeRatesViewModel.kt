package com.ivy.exchangeRates

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.persistence.entity.exchange.ExchangeRateOverrideEntity
import com.ivy.data.SyncState
import com.ivy.exchangeRates.action.RemoveOverriddenRateAct
import com.ivy.exchangeRates.action.WriteOverriddenRateAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject


@HiltViewModel
class ExchangeRatesViewModel @Inject constructor(
    private val removeOverriddenRateAct: RemoveOverriddenRateAct,
    private val writeOverriddenRateAct: WriteOverriddenRateAct,
    private val ratesFlow: RatesStateFlow
) : SimpleFlowViewModel<RatesState, RatesEvent>() {

    override val initialUi = RatesState(
        baseCurrency = "",
        manual = emptyList(),
        automatic = emptyList()
    )
    private val searchQuery = MutableStateFlow("")

    override val uiFlow: Flow<RatesState> = combine(
        ratesFlow(), searchQuery
    ) { ratesState, query ->
        if (query.isNotBlank()) {
            RatesState(
                baseCurrency = ratesState.baseCurrency,
                manual = ratesState.manual.filter {
                    it.to.contains(query, true)
                },
                automatic = ratesState.automatic.filter {
                    it.to.contains(query, true)
                }
            )

        } else {
            ratesState
        }
    }


    override suspend fun handleEvent(event: RatesEvent) {
        when (event) {
            is RatesEvent.RemoveOverride -> {
                handleRemoveOverride(event)
            }

            is RatesEvent.Search -> {
                searchQuery.value = event.query.trim()
            }

            is RatesEvent.UpdateRate -> {
                handleUpdateRate(event)
            }

            is RatesEvent.AddRate -> {
                handleAddRate(event)
            }
        }
    }

    private suspend fun handleRemoveOverride(event: RatesEvent.RemoveOverride) {
        removeOverriddenRateAct(
            RemoveOverriddenRateAct.Input(
                baseCurrency = event.rate.from,
                currency = event.rate.to
            )
        )
    }

    private suspend fun handleUpdateRate(event: RatesEvent.UpdateRate) {
        if (event.newRate > 0.0) {
            writeOverriddenRateAct(
                ExchangeRateOverrideEntity(
                    baseCurrency = event.rate.from,
                    currency = event.rate.to,
                    rate = event.newRate,
                    sync = SyncState.Synced,
                    lastUpdated = Instant.MIN
                )
            )
        }
    }

    private suspend fun handleAddRate(event: RatesEvent.AddRate) {
        if (event.rate.rate > 0.0) {
            writeOverriddenRateAct(
                ExchangeRateOverrideEntity(
                    baseCurrency = event.rate.from,
                    currency = event.rate.to,
                    rate = event.rate.rate,
                    sync = SyncState.Synced,
                    lastUpdated = Instant.MIN
                )
            )
        }
    }

}