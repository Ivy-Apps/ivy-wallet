package com.ivy.core.ui.currency

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.account.AccountsFlow
import com.ivy.core.ui.currency.data.CurrencyListItem
import com.ivy.core.ui.currency.data.CurrencyUi
import com.ivy.data.CurrencyCode
import com.ivy.data.IvyCurrency
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
internal class CurrencyPickerModalViewModel @Inject constructor(
    private val accountsFlow: AccountsFlow,
) : SimpleFlowViewModel<CurrencyModalState, CurrencyModalEvent>() {
    override val initialUi = CurrencyModalState(
        items = emptyList(),
        suggested = emptyList(),
        selectedCurrency = null,
        searchQuery = "",
    )

    private var searchQuery = MutableStateFlow("")
    private val selectedCurrency = MutableStateFlow<CurrencyUi?>(null)

    override val uiFlow: Flow<CurrencyModalState> = combine(
        currenciesFlow(), selectedCurrency, suggestedFlow()
    ) { currencies, selectedCurrency, suggested ->
        CurrencyModalState(
            items = currencies,
            suggested = suggested,
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
                            name = if (it.name.isNotEmpty())
                            // capitalize the first letter
                                "${it.name.first().uppercase()}${it.name.drop(1)}" else ""
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

    private fun suggestedFlow(): Flow<List<CurrencyCode>> = accountsFlow().map { accounts ->
        accounts.map { it.currency }.toSet()
    }.map { accountCurrencies ->
        accountCurrencies.plus(
            listOf(
                "USD",
                "EUR",
                "INR",
                "GBP"
            )
        ).toList().sorted()
    }

    // region Event Handling
    override suspend fun handleEvent(event: CurrencyModalEvent) = when (event) {
        is CurrencyModalEvent.Search -> handleSearch(event)
        is CurrencyModalEvent.SelectCurrency -> handleSelectCurrency(event)
        is CurrencyModalEvent.SelectCurrencyCode -> handleSelectCurrencyCode(event)
        is CurrencyModalEvent.Initial -> handleInitial(event)
    }

    private fun handleSearch(event: CurrencyModalEvent.Search) {
        searchQuery.value = event.query
    }

    private fun handleSelectCurrency(event: CurrencyModalEvent.SelectCurrency) {
        selectedCurrency.value = event.currencyUi
    }

    private fun handleSelectCurrencyCode(event: CurrencyModalEvent.SelectCurrencyCode) {
        findCurrency(event.currencyCode)?.let {
            selectedCurrency.value = it
        }
    }

    private fun handleInitial(event: CurrencyModalEvent.Initial) {
        findCurrency(event.initialCurrency)?.let {
            selectedCurrency.value = it
        }
    }

    private fun findCurrency(code: CurrencyCode): CurrencyUi? = (uiState.value.items
        .firstOrNull {
            (it as? CurrencyListItem.Currency)?.currency?.code == code
        } as? CurrencyListItem.Currency)?.currency
    // endregion
}