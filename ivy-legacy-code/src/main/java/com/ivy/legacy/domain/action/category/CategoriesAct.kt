package com.ivy.wallet.domain.action.category

import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.frp.then
import com.ivy.core.data.model.Category
import com.ivy.wallet.io.persistence.dao.CategoryDao
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class CategoriesAct @Inject constructor(
    private val categoryDao: CategoryDao
) : FPAction<Unit, ImmutableList<Category>>() {
    override suspend fun Unit.compose(): suspend () -> ImmutableList<Category> = suspend {
        io {
            categoryDao.findAll()
        }
    } thenMap { it.toDomain() } then { it.toImmutableList() }
}
