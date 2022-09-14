package com.ivy.sync.category
/*

import com.ivy.data.SyncMetadata
import com.ivy.data.category.Category
import com.ivy.frp.action.FPAction
import com.ivy.sync.base.SyncItem
import com.ivy.sync.ivyserver.category.CategoryIvyServerSync
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.persistence.dao.CategoryDao
import javax.inject.Inject

class SyncCategoriesAct @Inject constructor(
    private val categoryDao: CategoryDao, private val ivyServerSync: CategoryIvyServerSync
) : FPAction<IOEffect<List<Category>>, Unit>() {
    override suspend fun IOEffect<List<Category>>.compose(): suspend () -> Unit = {
        sync(this)
    }

    private suspend fun sync(operation: IOEffect<List<Category>>) {
        val sync = ivyServerSync.enabled() ?: return

        when (operation) {
            is IOEffect.Delete -> delete(sync, operation.item)
            is IOEffect.Save -> save(sync, operation.item)
        }
    }

    private suspend fun delete(sync: SyncItem<Category>, items: List<Category>) {
        sync.delete(items)
        // delete all locally not matter the result
        items.forEach { categoryDao.deleteById(it.id) }
    }

    private suspend fun save(sync: SyncItem<Category>, items: List<Category>) =
        sync.save(items)
            .map {
                val syncedItem = it.mark(
                    isSynced = true, isDeleted = false
                )
                persist(syncedItem)
            }

    private suspend fun persist(item: Category) {
        categoryDao.save(mapToEntity(item))
    }
}*/
