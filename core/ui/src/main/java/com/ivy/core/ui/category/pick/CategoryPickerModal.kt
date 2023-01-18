package com.ivy.core.ui.category.pick

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ivy.core.ui.R
import com.ivy.core.ui.category.create.CreateCategoryModal
import com.ivy.core.ui.category.pick.component.PickerCategoriesRow
import com.ivy.core.ui.category.pick.component.PickerParentCategory
import com.ivy.core.ui.category.pick.data.CategoryPickerItemUi
import com.ivy.core.ui.category.pick.data.SelectableCategoryUi
import com.ivy.core.ui.category.pick.data.dummySelectableCategoryUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.modal.ViewModelModal
import com.ivy.data.transaction.TransactionType
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview

@Composable
fun BoxScope.CategoryPickerModal(
    modal: IvyModal,
    level: Int = 1,
    trnType: TransactionType?,
    selected: CategoryUi?,
    onPick: (CategoryUi?) -> Unit,
) {
    val createCategoryModal = rememberIvyModal()

    ViewModelModal(
        modal = modal,
        provideViewModel = { hiltViewModel<CategoryPickerViewModel>() },
        previewState = { previewState() },
        level = level,
        actions = { _, onEvent ->
            IvyButton(
                size = ButtonSize.Small,
                visibility = Visibility.Medium,
                feeling = Feeling.Neutral,
                text = "Unspecified",
                icon = R.drawable.ic_custom_category_s,
            ) {
                onEvent(CategoryPickerEvent.CategorySelected(null))
                onPick(null)
                modal.hide()
            }
        }
    ) { state, onEvent ->
        LaunchedEffect(trnType) {
            onEvent(CategoryPickerEvent.Initial(trnType))
        }

        LaunchedEffect(selected) {
            onEvent(CategoryPickerEvent.CategorySelected(selected))
            onEvent(CategoryPickerEvent.CollapseParent)
        }


        LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
            item(key = "modal_title") {
                Title(text = stringResource(id = R.string.choose_category))
                SpacerVer(height = 16.dp)
            }
            pickerItems(
                items = state.items,
                onCategorySelect = {
                    onEvent(CategoryPickerEvent.CategorySelected(it))
                    onPick(it)
                    modal.hide()
                },
                onExpandParent = {
                    onEvent(CategoryPickerEvent.ExpandParent(it))
                },
            )
            item(key = "add_category_btn") {
                AddCategoryButton {
                    createCategoryModal.show()
                }
            }
            item(key = "last_item_space") {
                SpacerVer(height = 24.dp)
            }
        }
    }

    CreateCategoryModal(
        modal = createCategoryModal,
        level = level + 1,
    )
}

private fun LazyListScope.pickerItems(
    items: List<CategoryPickerItemUi>,
    onCategorySelect: (CategoryUi) -> Unit,
    onExpandParent: (SelectableCategoryUi) -> Unit,
) {
    items(
        items = items,
        key = {
            when (it) {
                is CategoryPickerItemUi.CategoriesRow -> it.categories.first().category.id
                is CategoryPickerItemUi.ParentCategory -> it.parent.category.id
            }
        }
    ) { item ->
        when (item) {
            is CategoryPickerItemUi.CategoriesRow -> {
                SpacerVer(height = 12.dp)
                PickerCategoriesRow(
                    categories = item.categories,
                    onSelect = { onCategorySelect(it.category) }
                )
            }
            is CategoryPickerItemUi.ParentCategory -> {
                SpacerVer(height = 12.dp)
                PickerParentCategory(
                    item = item,
                    onParentClick = {
                        if (item.expanded) {
                            onCategorySelect(item.parent.category)
                        } else {
                            onExpandParent(item.parent)
                        }
                    },
                    onChildClick = { onCategorySelect(it) }
                )
            }
        }
    }
}

@Composable
private fun AddCategoryButton(
    onClick: () -> Unit,
) {
    IvyButton(
        modifier = Modifier
            .padding(top = 12.dp)
            .padding(start = 12.dp),
        size = ButtonSize.Small,
        visibility = Visibility.Medium,
        feeling = Feeling.Positive,
        text = stringResource(R.string.add_category),
        icon = R.drawable.ic_round_add_24,
        onClick = onClick,
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        CategoryPickerModal(
            modal = modal,
            trnType = TransactionType.Expense,
            selected = dummyCategoryUi(),
            onPick = {}
        )
    }
}

private fun previewState() = CategoryPickerState(
    items = listOf(
        CategoryPickerItemUi.CategoriesRow(
            categories = listOf(
                dummySelectableCategoryUi(),
                dummySelectableCategoryUi(),
                dummySelectableCategoryUi(),
            )
        ),
        CategoryPickerItemUi.ParentCategory(
            parent = dummySelectableCategoryUi(),
            expanded = true,
            children = listOf(
                dummySelectableCategoryUi(),
                dummySelectableCategoryUi(),
            )
        ),
        CategoryPickerItemUi.ParentCategory(
            parent = dummySelectableCategoryUi(),
            expanded = false,
            children = listOf(
                dummySelectableCategoryUi(),
            )
        ),
        CategoryPickerItemUi.CategoriesRow(
            categories = listOf(
                dummySelectableCategoryUi(),
                dummySelectableCategoryUi(),
                dummySelectableCategoryUi(),
                dummySelectableCategoryUi(),
                dummySelectableCategoryUi(),
            )
        ),
        CategoryPickerItemUi.ParentCategory(
            parent = dummySelectableCategoryUi(),
            expanded = true,
            children = listOf(
                dummySelectableCategoryUi(),
                dummySelectableCategoryUi(),
                dummySelectableCategoryUi(),
            )
        ),
    )
)
// endregion