package com.ivy.core.ui.currency

import com.ivy.core.SimpleFlowViewModel
import com.ivy.core.ui.currency.data.CurrencyListItem
import com.ivy.core.ui.currency.data.CurrencyUi
import com.ivy.data.IvyCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
internal class CurrencyPickerModalViewModel @Inject constructor(
) : SimpleFlowViewModel<CurrencyModalState, CurrencyModalEvent>() {
    override val initialUi = CurrencyModalState(
        items = emptyList(),
        selectedCurrency = null,
        searchQuery = "",
    )

    private var searchQuery = MutableStateFlow("")
    private val selectedCurrency = MutableStateFlow<CurrencyUi?>(null)

    override val uiFlow: Flow<CurrencyModalState> =
        combine(currenciesFlow(), selectedCurrency) { currencies, selectedCurrency ->
            CurrencyModalState(
                items = currencies,
                selectedCurrency = selectedCurrency,
                searchQuery = searchQuery.value,
            )
        }

    private fun currenciesFlow(): Flow<List<CurrencyListItem>> = combine(
        availableCurrenciesFlow(), searchQueryFlow()
    ) { allCurrencies, searchQuery ->
        val currencies = if (searchQuery != null)
            allCurrencies.filter { it.passesSearch(searchQuery) }
        else allCurrencies

        currencies.groupBy { it.code.first() }
            .toSortedMap()
            .flatMap { (letter, currencies) ->
                listOf(
                    CurrencyListItem.SectionDivider(name = letter.uppercase()),
                ) + currencies.map {
                    CurrencyListItem.Currency(
                        CurrencyUi(
                            code = it.code,
                            name = it.name
                        )
                    )
                }
            }
    }

    private fun IvyCurrency.passesSearch(searchQuery: String): Boolean =
        code.lowercase().contains(searchQuery) || name.lowercase().contains(searchQuery)

    @OptIn(FlowPreview::class)
    private fun searchQueryFlow(): Flow<String?> = searchQuery.map {
        it.lowercase().trim().takeIf(String::isNotEmpty) // normalize search query
    }.debounce(100)

    private fun availableCurrenciesFlow(): Flow<List<IvyCurrency>> =
        flowOf(IvyCurrency.getAvailable())


    // region Event Handling
    override suspend fun handleEvent(event: CurrencyModalEvent) = when (event) {
        is CurrencyModalEvent.Search -> handleSearch(event)
        is CurrencyModalEvent.SelectCurrency -> handleSelectCurrency(event)
        is CurrencyModalEvent.Initial -> handleInitial(event)
    }

    private fun handleSearch(event: CurrencyModalEvent.Search) {
        searchQuery.value = event.query
    }

    private fun handleSelectCurrency(event: CurrencyModalEvent.SelectCurrency) {
        selectedCurrency.value = event.currencyUi
    }

    private fun handleInitial(event: CurrencyModalEvent.Initial) {
        val currencyItem = uiState.value.items.firstOrNull {
            (it as? CurrencyListItem.Currency)?.currency?.code == event.initialCurrency
        } as? CurrencyListItem.Currency
        if (currencyItem != null) {
            selectedCurrency.value = currencyItem.currency
        }
    }
    // endregion
}