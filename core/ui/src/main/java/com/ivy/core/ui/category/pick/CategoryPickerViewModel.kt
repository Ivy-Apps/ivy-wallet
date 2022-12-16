package com.ivy.core.ui.category.pick

import com.ivy.core.domain.SimpleFlowViewModel
import com.ivy.core.domain.action.category.CategoriesListFlow
import com.ivy.core.ui.action.mapping.MapCategoryUiAct
import com.ivy.core.ui.data.CategoryUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class CategoryPickerViewModel @Inject constructor(
    categoriesListFlow: CategoriesListFlow,
    private val mapCategoryUiAct: MapCategoryUiAct,
) : SimpleFlowViewModel<CategoryPickerState, CategoryPickerEvent>() {
    override val initialUi = CategoryPickerState(
        items = emptyList()
    )

    private val expandedParent = MutableStateFlow<CategoryUi?>(null)
    private val selectedCategory = MutableStateFlow<CategoryUi?>(null)

    override val uiFlow: Flow<CategoryPickerState> = combine(
        categoriesListFlow(Unit), selectedCategory, expandedParent
    ) { items, selectedCategory, expandedParent ->
//        items.mapNotNull { item ->
//            when (item) {
//                is CategoryListItem.Archived -> null
//                is CategoryListItem.CategoryHolder -> CategoryPickerItemUi.CategoryCard(
//                    category = mapCategoryUiAct(item.category),
//                    selected = item.category.id.toString() == selectedCategory?.id,
//                )
//                is CategoryListItem.ParentCategory -> CategoryPickerItemUi.ParentCategory(
//                    parent = mapCategoryUiAct(item.parent),
//                    expanded = expandedParent?.id == item.parent.id.toString(),
//                    selected = item.parent.id.toString() == selectedCategory?.id,
//                    children = item.children.map {
//                        CategoryPickerItemUi.CategoryCard(
//                            category = mapCategoryUiAct(it),
//                            selected = it.id.toString() == selectedCategory?.id,
//                        )
//                    }
//                )
//            }
//        }

        TODO()
    }.map { items ->
        CategoryPickerState(items = items)
    }


    // region Event Handling
    override suspend fun handleEvent(event: CategoryPickerEvent) = TODO()

    // endregion
}