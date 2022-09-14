package com.ivy.core.domain.action.category

import com.ivy.core.domain.functions.mapping.entity.mapToEntity
import com.ivy.core.persistence.dao.category.CategoryDao
import com.ivy.data.Modify
import com.ivy.data.SyncState
import com.ivy.data.category.Category
import com.ivy.frp.action.FPAction
import com.ivy.sync.SyncTask
import com.ivy.sync.syncTaskFrom
import javax.inject.Inject

class WriteCategoriesAct @Inject constructor(
    private val categoryDao: CategoryDao,
//    private val syncCategoriesAct: SyncCategoriesAct
) : FPAction<Modify<Category>, SyncTask>() {

    override suspend fun Modify<Category>.compose(): suspend () -> SyncTask = {
        when (this) {
            is Modify.Delete -> delete(itemIds)
            is Modify.Save -> save(items)
        }

        // TODO: Implement sync
        syncTaskFrom {}
    }

    private suspend fun delete(categoryIds: List<String>) {
        categoryDao.updateSync(
            categoryIds = categoryIds,
            sync = SyncState.Deleting
        )
    }

    private suspend fun save(categories: List<Category>) {
        categoryDao.save(
            categories.map { mapToEntity(it).copy(sync = SyncState.Syncing) }
        )
    }

}