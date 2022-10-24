package com.ivy.core.domain.action.category

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.data.CategoryListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriesListFlow @Inject constructor(
    private val categoriesFlow: CategoriesFlow,
) : FlowAction<Unit, List<CategoryListItem>>() {
    override fun Unit.createFlow(): Flow<List<CategoryListItem>> =
        categoriesFlow().map { categories ->
            val categoriesWithParent = categories.filter { it.parentCategoryId != null }
                .associateBy { it.parentCategoryId }
            TODO()
        }
}