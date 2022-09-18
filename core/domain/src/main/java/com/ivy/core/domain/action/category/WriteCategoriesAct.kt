package com.ivy.core.domain.action.category

import com.ivy.core.domain.action.data.Modify
import com.ivy.core.domain.pure.mapping.entity.mapToEntity
import com.ivy.core.persistence.dao.category.CategoryDao
import com.ivy.data.SyncState
import com.ivy.data.category.Category
import com.ivy.frp.action.Action
import javax.inject.Inject

class WriteCategoriesAct @Inject constructor(
    private val categoryDao: CategoryDao,
) : Action<Modify<Category>, Unit>() {
    companion object {
        fun save(category: Category) = Modify.Save(listOf(category))
        fun saveMany(categories: Iterable<Category>) = Modify.Save(categories.toList())

        fun delete(categoryId: String) = Modify.Delete<Category>(listOf(categoryId))
        fun deleteMany(categoryIds: Iterable<String>) =
            Modify.Delete<Category>(categoryIds.toList())
    }

    override suspend fun Modify<Category>.willDo() {
        when (this) {
            is Modify.Delete -> delete(itemIds)
            is Modify.Save -> save(items)
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
            categories.map { mapToEntity(it).copy(sync = SyncState.Syncing) }
        )
    }

}