package com.ivy.core.domain.action.category

import com.ivy.common.toUUID
import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.persistence.dao.category.CategoryDao
import com.ivy.core.persistence.entity.category.CategoryEntity
import com.ivy.data.category.Category
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @return a flow of latest [Category]ies by transforming db entities into domain objects.
 */
@Singleton
class CategoriesFlow @Inject constructor(
    private val categoryDao: CategoryDao,
) : SharedFlowAction<List<Category>>() {
    override fun initialValue(): List<Category> = emptyList()

    override fun createFlow(): Flow<List<Category>> =
        categoryDao.findAll().map { entities ->
            entities.map { toDomain(it) }
        }.flowOn(Dispatchers.Default)
}

fun toDomain(it: CategoryEntity) = Category(
    id = it.id.toUUID(),
    name = it.name,
    parentCategoryId = it.parentCategoryId?.toUUID(),
    color = it.color,
    icon = it.icon,
    orderNum = it.orderNum,
    sync = it.sync,
    type = it.type,
    state = it.state,
)