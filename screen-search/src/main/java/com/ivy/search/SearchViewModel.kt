package com.ivy.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()

    private val _transactions =
        MutableStateFlow<ImmutableList<TransactionHistoryItem>>(persistentListOf())
    val transactions = _transactions.asStateFlow()

    private val _accounts = MutableStateFlow<ImmutableList<Account>>(persistentListOf())
    val accounts = _accounts.asStateFlow()

    private val _categories = MutableStateFlow<ImmutableList<Category>>(persistentListOf())
    val categories = _categories.asStateFlow()

    fun search(query: String) {
        val normalizedQuery = query.lowercase().trim()

        viewModelScope.launch {
            TestIdlingResource.increment()

            _baseCurrencyCode.value = baseCurrencyAct(Unit)

            _categories.value = categoriesAct(Unit)

            _accounts.value = accountsAct(Unit)

            _transactions.value = ioThread {
                val trns = allTrnsAct(Unit)
                    .filter {
                        it.title.matchesQuery(normalizedQuery) ||
                                it.description.matchesQuery(normalizedQuery)
                    }
                trnsWithDateDivsAct(
                    TrnsWithDateDivsAct.Input(
                        baseCurrency = baseCurrencyCode.value,
                        transactions = trns
                    )
                ).toImmutableList()
            }

            TestIdlingResource.decrement()
        }
    }

    private fun String?.matchesQuery(query: String): Boolean {
        return this?.lowercase()?.trim()?.contains(query) == true
    }
}
