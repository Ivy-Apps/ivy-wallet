package com.ivy.core.action.category

import com.ivy.data.SyncMetadata
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryMetadata
import com.ivy.frp.action.FPAction
import com.ivy.frp.thenInvokeAfter
import com.ivy.state.categoriesUpdate
import com.ivy.state.writeIvyState
import com.ivy.wallet.io.persistence.dao.CategoryDao
import javax.inject.Inject

class CategoriesAct @Inject constructor(
    private val categoryDao: CategoryDao
) : FPAction<Unit, List<Category>>() {
    override suspend fun Unit.compose(): suspend () -> List<Category> = {
        // TODO: enable caching
        // readIvyState().categories ?: loadCategories()
        loadCategories()
    }

    private suspend fun loadCategories(): List<Category> = suspend {
        categoryDao.findAll().map {
            Category(
                id = it.id,
                name = it.name,
                parentCategoryId = it.parentCategoryId,
                color = it.color,
                icon = it.icon,
                metadata = CategoryMetadata(
                    orderNum = it.orderNum,
                    sync = SyncMetadata(
                        isSynced = it.isSynced,
                        isDeleted = it.isDeleted
                    )
                )
            )
        }
    } thenInvokeAfter { categories ->
        writeIvyState(categoriesUpdate(newCategories = categories))
        categories
    }
}