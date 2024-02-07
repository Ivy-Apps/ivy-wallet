package com.ivy.data

import com.ivy.data.model.Account
import com.ivy.data.model.Category
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.CategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InMemoryDataStore @Inject constructor(
    private val dataWriteEventBus: DataWriteEventBus,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
) {
    private val _accounts = MutableStateFlow(emptyList<Account>())
    private val _categories = MutableStateFlow(emptyList<Category>())

    val accounts: StateFlow<List<Account>> = _accounts
    val categories: StateFlow<List<Category>> = _categories

    fun init(scope: CoroutineScope) {
        scope.updateAccounts()
        scope.updateCategories()
        scope.launch {
            // TODO: This can be optimized. But let's not do premature optimization.
            dataWriteEventBus.events.collectLatest { event ->
                when (event) {
                    is DataWriteEvent.SaveAccounts,
                    is DataWriteEvent.DeleteAccounts -> {
                        scope.updateAccounts()
                    }

                    is DataWriteEvent.DeleteCategories,
                    is DataWriteEvent.SaveCategories -> {
                        scope.updateCategories()
                    }
                }
            }
        }
    }

    private fun CoroutineScope.updateAccounts() {
        launch {
            _accounts.value = accountRepository.findAll()
        }
    }

    private fun CoroutineScope.updateCategories() {
        launch {
            _categories.value = categoryRepository.findAll()
        }
    }
}