package com.ivy.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.core.ComposeViewModel
import com.ivy.core.datamodel.Account
import com.ivy.core.datamodel.Category
import com.ivy.core.datamodel.TransactionHistoryItem
import com.ivy.frp.test.TestIdlingResource
import com.ivy.legacy.utils.getDefaultFIATCurrency
import com.ivy.legacy.utils.ioThread
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.AllTrnsAct
import com.ivy.wallet.domain.action.transaction.TrnsWithDateDivsAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val allTrnsAct: AllTrnsAct
) : ComposeViewModel<SearchState, SearchEvent>() {

    private val _uiState = mutableStateOf(
        SearchState(
            transactions = persistentListOf(),
            baseCurrency = getDefaultFIATCurrency().currencyCode,
            accounts = persistentListOf(),
            categories = persistentListOf()
        )
    )

    init {
        search("")
    }

    @Composable
    override fun uiState(): SearchState {
        return _uiState.value
    }

    override fun onEvent(event: SearchEvent) {
        when(event) {
            is SearchEvent.Search -> search(event.query)
        }
    }

    private fun search(query: String) {
        val normalizedQuery = query.lowercase().trim()

        viewModelScope.launch {
            val queryResult = ioThread {
                val filteredTransactions = allTrnsAct(Unit)
                    .filter { transaction ->
                        transaction.title.matchesQuery(normalizedQuery) ||
                                transaction.description.matchesQuery(normalizedQuery)
                    }
                trnsWithDateDivsAct(
                    TrnsWithDateDivsAct.Input(
                        baseCurrency = baseCurrencyAct(Unit),
                        transactions = filteredTransactions
                    )
                ).toImmutableList()
            }

            _uiState.value = _uiState.value.copy(
                transactions = queryResult,
                baseCurrency = baseCurrencyAct(Unit),
                accounts = accountsAct(Unit),
                categories = categoriesAct(Unit)
            )
        }
    }

    private fun String?.matchesQuery(query: String): Boolean {
        return this?.lowercase()?.trim()?.contains(query) == true
    }
}
