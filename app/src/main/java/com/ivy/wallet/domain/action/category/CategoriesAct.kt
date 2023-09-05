package com.ivy.wallet.domain.action.category

import com.google.firebase.crashlytics.internal.model.ImmutableList
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.frp.then
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.utils.toActualImmutableList
import javax.inject.Inject

class CategoriesAct @Inject constructor(
    private val categoryDao: CategoryDao
) : FPAction<Unit, ImmutableList<Category>>() {
    override suspend fun Unit.compose(): suspend () -> ImmutableList<Category> = suspend {
        io {
            categoryDao.findAll()
        }
    } thenMap { it.toDomain() } then {
        it.toActualImmutableList()
    }
}