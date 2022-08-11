package com.ivy.core.action.category

import com.ivy.data.category.Category
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.state.categoriesUpdate
import com.ivy.state.invalidate
import com.ivy.state.writeIvyState
import com.ivy.sync.SyncTask
import com.ivy.sync.category.SyncCategoryAct
import com.ivy.sync.category.mark
import com.ivy.sync.syncTaskFrom
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.CategoryDao
import javax.inject.Inject

class WriteCategoryAct @Inject constructor(
    private val categoryDao: CategoryDao,
    private val syncCategoryAct: SyncCategoryAct
) : FPAction<IOEffect<Category>, SyncTask>() {
    override suspend fun IOEffect<Category>.compose(): suspend () -> SyncTask = {
        when (this) {
            is IOEffect.Delete -> persist(
                item = item.mark(
                    isSynced = false,
                    isDeleted = true
                )
            )
            is IOEffect.Save -> persist(
                item = item.mark(
                    isSynced = false,
                    isDeleted = false
                )
            )
        }

        // Invalidate cache
        writeIvyState(categoriesUpdate(invalidate()))

        syncTaskFrom(this asParamTo syncCategoryAct)
    }

    private suspend fun persist(item: Category) = categoryDao.save(mapToEntity(item))
}