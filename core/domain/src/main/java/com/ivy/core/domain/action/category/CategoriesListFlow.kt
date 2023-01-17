package com.ivy.core.domain.action.category

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.data.CategoryListItem
import com.ivy.data.category.Category
import com.ivy.data.category.CategoryState
import com.ivy.data.category.CategoryType
import com.ivy.data.transaction.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

class CategoriesListFlow @Inject constructor(
    private val categoriesFlow: CategoriesFlow,
) : FlowAction<CategoriesListFlow.Input, List<CategoryListItem>>() {
    /**
     * @param trnType - null for all categories
     */
    data class Input(
        val trnType: TransactionType?,
    )

    override fun createFlow(input: Input): Flow<List<CategoryListItem>> =
        categoriesFlow()
            // Filter only categories that match the selected transaction type
            .map { categories ->
                if (input.trnType != null) {
                    categories.filter {
                        when (it.type) {
                            CategoryType.Income -> input.trnType == TransactionType.Income
                            CategoryType.Expense -> input.trnType == TransactionType.Expense
                            CategoryType.Both -> true
                        }
                    }
                } else categories
            }
            .map { categories ->
                val archived = mutableListOf<Category>()
                val parents = mutableListOf<Category>()
                val subcategories = mutableMapOf<UUID, MutableList<Category>>()

                categories.forEach {
                    if (it.state == CategoryState.Archived) {
                        archived.add(it)
                        return@forEach
                    }
                    val parentCategoryId = it.parentCategoryId
                    if (parentCategoryId == null) {
                        parents.add(it)
                    } else {
                        subcategories.computeIfAbsent(parentCategoryId) {
                            mutableListOf()
                        }
                        subcategories[parentCategoryId]!!.add(it)
                    }
                }

                val notArchived = parents.map { parent ->
                    val children = subcategories[parent.id]?.takeIf { it.isNotEmpty() }
                    subcategories.remove(parent.id)

                    if (children != null) {
                        CategoryListItem.ParentCategory(
                            parent = parent,
                            children = children.sortedBy { it.orderNum }
                        )
                    } else {
                        CategoryListItem.CategoryHolder(
                            parent
                        )
                    }
                } + subcategories.values.flatten().map {
                    CategoryListItem.CategoryHolder(it)
                }

                val allItems = if (archived.isNotEmpty())
                    notArchived + CategoryListItem.Archived(
                        archived.sortedBy { it.orderNum }
                    ) else notArchived

                allItems.sortedBy {
                    when (it) {
                        is CategoryListItem.Archived -> Double.MAX_VALUE - 10
                        is CategoryListItem.CategoryHolder -> it.category.orderNum
                        is CategoryListItem.ParentCategory -> it.parent.orderNum
                    }
                }
            }
}