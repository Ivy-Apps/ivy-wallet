package com.ivy.wallet.domain.action.category

import com.ivy.data.Category
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.io.persistence.dao.CategoryDao
import javax.inject.Inject

class CategoriesAct @Inject constructor(
    private val categoryDao: CategoryDao
) : FPAction<Unit, List<Category>>() {
    override suspend fun Unit.compose(): suspend () -> List<Category> = suspend {
        io {
            categoryDao.findAll()
        }
    } thenMap { it.toDomain() }
}