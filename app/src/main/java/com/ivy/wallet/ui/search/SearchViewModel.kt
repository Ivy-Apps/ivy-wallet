package com.ivy.wallet.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ivy.wallet.base.TestIdlingResource
import com.ivy.wallet.base.getDefaultFIATCurrency
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.logic.withDateDividers
import com.ivy.wallet.model.TransactionHistoryItem
import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.model.entity.Transaction
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.persistence.dao.SettingsDao
import com.ivy.wallet.persistence.dao.TransactionDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val transactionDao: TransactionDao,
    private val settingsDao: SettingsDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao,
    private val exchangeRatesLogic: ExchangeRatesLogic
) : ViewModel() {

    private val _baseCurrencyCode = MutableStateFlow(getDefaultFIATCurrency().currencyCode)
    val baseCurrencyCode = _baseCurrencyCode.asStateFlow()

    private val _transactions = MutableStateFlow(emptyList<TransactionHistoryItem>())
    val transactions = _transactions.asStateFlow()

    private val _accounts = MutableStateFlow(emptyList<Account>())
    val accounts = _accounts.asStateFlow()

    private val _categories = MutableStateFlow(emptyList<Category>())
    val categories = _categories.asStateFlow()

    fun search(query: String) {
        val normalizedQuery = query.lowercase().trim()

        viewModelScope.launch {
            TestIdlingResource.increment()

            _baseCurrencyCode.value = ioThread {
                settingsDao.findFirst().currency
            }

            _categories.value = ioThread {
                categoryDao.findAll()
            }

            _accounts.value = ioThread {
                accountDao.findAll()
            }

            _transactions.value = ioThread {
                transactionDao.findAll()
                    .filter {
                        it.title.matchesQuery(normalizedQuery) ||
                                it.description.matchesQuery(normalizedQuery)
                    }.withDateDividers(
                        exchangeRatesLogic = exchangeRatesLogic,
                        accountDao = accountDao,
                        settingsDao = settingsDao
                    )
            }

            TestIdlingResource.decrement()
        }
    }

    private fun String?.matchesQuery(query: String): Boolean {
        return this?.lowercase()?.trim()?.contains(query) == true
    }
}