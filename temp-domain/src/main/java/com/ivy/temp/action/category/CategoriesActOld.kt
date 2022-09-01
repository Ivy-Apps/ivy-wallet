package com.ivy.wallet.domain.action.category

import com.ivy.data.CategoryOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.io.persistence.dao.CategoryDao
import javax.inject.Inject

@Deprecated("Use CategoriesAct from `:core:actions`")
class CategoriesActOld @Inject constructor(
    private val categoryDao: CategoryDao
) : FPAction<Unit, List<CategoryOld>>() {
    override suspend fun Unit.compose(): suspend () -> List<CategoryOld> = suspend {
        io {
            categoryDao.findAllSuspend()
        }
    } thenMap { it.toDomain() }
}