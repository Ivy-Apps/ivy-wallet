package com.ivy.core.ui.category.pick.action

import com.ivy.core.domain.action.FlowAction
import com.ivy.core.domain.action.category.CategoriesListFlow
import com.ivy.core.domain.action.data.CategoryListItem
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.category.pick.data.CategoryPickerItemUi
import com.ivy.core.ui.category.pick.data.SelectableCategoryUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.data.transaction.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CategoryPickerItemsFlow @Inject constructor(
    private val categoriesListFlow: CategoriesListFlow,
    private val mapCategoryUiAct: MapCategoryUiAct,
) : FlowAction<CategoryPickerItemsFlow.Input, List<CategoryPickerItemUi>>() {
    data class Input(
        val selectedCategory: CategoryUi?,
        val expandedParent: CategoryUi?,
        val trnType: TransactionType?,
    )

    override fun createFlow(input: Input): Flow<List<CategoryPickerItemUi>> =
        categoriesListFlow(CategoriesListFlow.Input(trnType = input.trnType))
            .map { items ->
                items.mapNotNull { item ->
                    when (item) {
                        is CategoryListItem.Archived -> null
                        is CategoryListItem.CategoryHolder -> SelectableCategoryUi(
                            category = mapCategoryUiAct(item.category),
                            selected = item.category.id.toString() == input.selectedCategory?.id,
                        )
                        is CategoryListItem.ParentCategory -> {
                            val hasSelectedChild = item.children.any {
                                it.id.toString() == input.selectedCategory?.id
                            }
                            CategoryPickerItemUi.ParentCategory(
                                parent = SelectableCategoryUi(
                                    category = mapCategoryUiAct(item.parent),
                                    selected = item.parent.id.toString() == input.selectedCategory?.id ||
                                            hasSelectedChild,
                                ),
                                expanded = input.expandedParent?.id == item.parent.id.toString() ||
                                        hasSelectedChild,
                                children = item.children.map {
                                    SelectableCategoryUi(
                                        category = mapCategoryUiAct(it),
                                        selected = it.id.toString() == input.selectedCategory?.id,
                                    )
                                }
                            )
                        }
                    }
                }
            }.map { data ->
                val res = mutableListOf<CategoryPickerItemUi>()
                var catsRowAccumulator = mutableListOf<SelectableCategoryUi>()

                data.forEach {
                    when (it) {
                        is CategoryPickerItemUi.ParentCategory -> {
                            if (catsRowAccumulator.isNotEmpty()) {
                                res.add(CategoryPickerItemUi.CategoriesRow(catsRowAccumulator))
                                catsRowAccumulator = mutableListOf()
                            }
                            res.add(it)
                        }
                        is SelectableCategoryUi -> catsRowAccumulator.add(it)
                    }
                }

                if (catsRowAccumulator.isNotEmpty()) {
                    res.add(CategoryPickerItemUi.CategoriesRow(catsRowAccumulator))
                }

                res
            }
}