package com.ivy.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.ivy.base.legacy.TransactionHistoryItem
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.repository.TransactionRepository
import com.ivy.domain.ComposeViewModel
import com.ivy.legacy.datamodel.Account
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.utils.getDefaultFIATCurrency
import com.ivy.legacy.utils.ioThread
import com.ivy.wallet.domain.action.account.AccountsAct
import com.ivy.wallet.domain.action.category.CategoriesAct
import com.ivy.wallet.domain.action.settings.BaseCurrencyAct
import com.ivy.wallet.domain.action.transaction.TrnsWithDateDivsAct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val accountsAct: AccountsAct,
    private val categoriesAct: CategoriesAct,
    private val baseCurrencyAct: BaseCurrencyAct,
    private val transactionRepository: TransactionRepository,
) : ComposeViewModel<SearchState, SearchEvent>() {

    private val transactions =
        mutableStateOf<ImmutableList<TransactionHistoryItem>>(persistentListOf())
    private val baseCurrency = mutableStateOf<String>(getDefaultFIATCurrency().currencyCode)
    private val accounts = mutableStateOf<ImmutableList<Account>>(persistentListOf())
    private val categories = mutableStateOf<ImmutableList<Category>>(persistentListOf())
    private val searchQuery = mutableStateOf("")

    @Composable
    override fun uiState(): SearchState {
        LaunchedEffect(Unit) {
            search(searchQuery.value)
        }

        return SearchState(
            searchQuery = searchQuery.value,
            transactions = transactions.value,
            baseCurrency = baseCurrency.value,
            accounts = accounts.value,
            categories = categories.value
        )
    }

    override fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.Search -> search(event.query)
        }
    }

    private fun search(query: String) {
        searchQuery.value = query
        val normalizedQuery = query.lowercase().trim()

        viewModelScope.launch {
            val queryResult = ioThread {
                val filteredTransactions = transactionRepository.findAll()
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

            transactions.value = queryResult
            baseCurrency.value = baseCurrencyAct(Unit)
            accounts.value = accountsAct(Unit)
            categories.value = categoriesAct(Unit)
        }
    }

    private fun NotBlankTrimmedString?.matchesQuery(query: String): Boolean {
        return this?.toString()?.lowercase()?.trim()?.contains(query) == true
    }
}
