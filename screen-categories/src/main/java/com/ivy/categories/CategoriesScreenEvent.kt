package com.ivy.categories

import com.ivy.wallet.domain.data.SortOrder
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData

sealed interface CategoriesScreenEvent {
  data class OnReorder(
    val newOrder: List<CategoryData>,
    val sortOrder: SortOrder = SortOrder.DEFAULT
  ) : CategoriesScreenEvent

  data class OnCreateCategory(val createCategoryData: CreateCategoryData) :
    CategoriesScreenEvent

  data class OnReorderModalVisible(val visible: Boolean) : CategoriesScreenEvent
  data class OnSortOrderModalVisible(val visible: Boolean) : CategoriesScreenEvent
  data class OnCategoryModalVisible(val categoryModalData: CategoryModalData?) :
    CategoriesScreenEvent
}