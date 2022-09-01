package com.ivy.core.action.category

import com.ivy.core.action.SharedFlowAction
import com.ivy.core.action.icon.DefaultTo
import com.ivy.core.action.icon.IconAct
import com.ivy.data.SyncMetadata
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryMetadata
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.data.CategoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesFlow @Inject constructor(
    private val categoryDao: CategoryDao,
    private val iconAct: IconAct
) : SharedFlowAction<List<Category>>() {
    override suspend fun initialValue(): List<Category> = emptyList()

    override suspend fun createFlow(): Flow<List<Category>> =
        categoryDao.findAll().map { entities ->
            entities.map { toCategory(it) }
        }

    private suspend fun toCategory(it: CategoryEntity) = Category(
        id = it.id,
        name = it.name,
        parentCategoryId = it.parentCategoryId,
        color = it.color,
        icon = iconAct(
            IconAct.Input(
                iconId = it.icon, defaultTo = DefaultTo.Category
            )
        ),
        metadata = CategoryMetadata(
            orderNum = it.orderNum, sync = SyncMetadata(
                isSynced = it.isSynced, isDeleted = it.isDeleted
            )
        )
    )
}