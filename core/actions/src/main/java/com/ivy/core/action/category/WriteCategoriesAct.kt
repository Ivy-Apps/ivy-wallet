package com.ivy.core.action.category

import com.ivy.data.category.Category
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.state.categoriesUpdate
import com.ivy.state.invalidate
import com.ivy.state.writeIvyState
import com.ivy.sync.SyncTask
import com.ivy.sync.category.SyncCategoriesAct
import com.ivy.sync.category.mark
import com.ivy.sync.syncTaskFrom
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.CategoryDao
import javax.inject.Inject

class WriteCategoriesAct @Inject constructor(
    private val categoryDao: CategoryDao,
    private val syncCategoriesAct: SyncCategoriesAct
) : FPAction<IOEffect<List<Category>>, SyncTask>() {

    override suspend fun IOEffect<List<Category>>.compose(): suspend () -> SyncTask = {
        when (this) {
            is IOEffect.Delete -> persist(items = item.map {
                it.mark(
                    isSynced = false, isDeleted = true
                )
            })
            is IOEffect.Save -> persist(items = item.map {
                it.mark(
                    isSynced = false, isDeleted = false
                )
            })
        }

        // Invalidate cache
        writeIvyState(categoriesUpdate(invalidate()))

        syncTaskFrom(this asParamTo syncCategoriesAct)
    }

    private suspend fun persist(items: List<Category>) = categoryDao.save(items.map(::mapToEntity))
}