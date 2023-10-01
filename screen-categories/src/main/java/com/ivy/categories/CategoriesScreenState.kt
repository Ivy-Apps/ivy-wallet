package com.ivy.categories

import com.ivy.wallet.domain.data.SortOrder
import com.ivy.wallet.ui.theme.modal.edit.CategoryModalData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

data class CategoriesScreenState(
  val baseCurrency: String = "",
  val categories: ImmutableList<CategoryData> = persistentListOf(),
  val reorderModalVisible: Boolean = false,
  val categoryModalData: CategoryModalData? = null,
  val sortModalVisible: Boolean = false,
  val sortOrderItems: ImmutableList<SortOrder> = SortOrder.values().toList().toImmutableList(),
  val sortOrder: SortOrder = SortOrder.DEFAULT
)