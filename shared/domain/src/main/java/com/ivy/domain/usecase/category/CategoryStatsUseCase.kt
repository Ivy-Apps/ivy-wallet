package com.ivy.domain.usecase.category

import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.model.AccountId
import com.ivy.data.model.CategoryId
import com.ivy.data.model.Transaction
import com.ivy.domain.model.AccountStats
import com.ivy.domain.model.CategoryStats
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CategoryStatsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider
) {
    suspend fun calculate(
        category: CategoryId,
        transactions: List<Transaction>
    ): CategoryStats = withContext(dispatchers.default) {
        TODO("Not implemented")
    }
}