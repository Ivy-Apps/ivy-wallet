package com.ivy.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.data.AccountOld
import com.ivy.data.CategoryOld
import com.ivy.data.getDefaultFIATCurrency
import com.ivy.frp.test.TestIdlingResource
import com.ivy.wallet.domain.action.account.AccountsActOld
import com.ivy.wallet.domain.action.category.CategoriesActOld
import com.ivy.wallet.domain.action.settings.BaseCurrencyActOld
import com.ivy.wallet.domain.action.transaction.AllTrnsAct
import com.ivy.wallet.domain.action.transaction.TrnsWithDateDivsAct
import com.ivy.wallet.utils.ioThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct,
    private val accountsAct: AccountsActOld,
    private val categoriesAct: CategoriesActOld,
    private val baseCurrencyAct: BaseCurrencyActOld,
    private val allTrnsAct: AllTrnsAct
) : ViewModel() {

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()

    private val _transactions = MutableStateFlow(emptyList<Any>())
    val transactions = _transactions.asStateFlow()

    private val _accounts = MutableStateFlow(emptyList<AccountOld>())
    val accounts = _accounts.asStateFlow()

    private val _categories = MutableStateFlow(emptyList<CategoryOld>())
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
                )
            }

            TestIdlingResource.decrement()
        }
    }

    private fun String?.matchesQuery(query: String): Boolean {
        return this?.lowercase()?.trim()?.contains(query) == true
    }
}