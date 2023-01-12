package com.ivy.core.domain.action.category

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.pure.mapping.entity.mapToEntity
import com.ivy.core.persistence.dao.category.CategoryDao
import com.ivy.data.SyncState
import com.ivy.data.category.Category
import javax.inject.Inject

/**
 * Persists _(saves or deletes)_ categories locally. See [Modify].
 *
 * Use [Modify.save], [Modify.saveMany], [Modify.delete] or [Modify.deleteMany].
 */
class WriteCategoriesAct @Inject constructor(
    private val categoryDao: CategoryDao,
    private val timeProvider: TimeProvider,
) : Action<Modify<Category>, Unit>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(modify: Modify<Category>) {
        when (modify) {
            is Modify.Delete -> delete(modify.itemIds)
            is Modify.Save -> save(modify.items)
        }
    }

    private suspend fun delete(categoryIds: List<String>) {
        categoryDao.updateSync(
            categoryIds = categoryIds,
            sync = SyncState.Deleting
        )
    }

    private suspend fun save(categories: List<Category>) {
        categoryDao.save(
            categories
                .filter { it.name.isNotBlank() }
                .map { it.copy(name = it.name.trim()) }
                .map {
                    mapToEntity(it, timeProvider = timeProvider)
                        .copy(sync = SyncState.Syncing)
                }
        )
    }

}