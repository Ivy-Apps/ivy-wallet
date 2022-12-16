package com.ivy.core.ui.category.pick

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.category.pick.component.PickerCategoriesRow
import com.ivy.core.ui.category.pick.data.CategoryPickerItemUi
import com.ivy.core.ui.category.pick.data.SelectableCategoryUi
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@Composable
fun BoxScope.CategoryPickerModal(
    modal: IvyModal,
    level: Int = 1,
    selected: CategoryUi,
    onSelect: (CategoryUi?) -> Unit,
) {
    val viewModel: CategoryPickerViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel = viewModel, preview = ::previewState)

    LaunchedEffect(selected) {
        viewModel?.onEvent(CategoryPickerEvent.CategorySelected(selected))
    }

    Modal(
        modal = modal,
        level = level,
        actions = {
            IvyButton(
                size = ButtonSize.Small,
                visibility = Visibility.High,
                feeling = Feeling.Neutral,
                text = "Unspecified",
                icon = R.drawable.ic_custom_category_s,
            ) {
                onSelect(null)
                modal.hide()
            }
        }
    ) {
        LazyColumn {
            item(key = "title") {
                Title(text = stringResource(id = R.string.choose_category))
                SpacerVer(height = 16.dp)
            }
        }
    }
}

private fun LazyListScope.pickerItems(
    items: List<CategoryPickerItemUi>,
    onCategorySelect: (CategoryUi?) -> Unit,
    onParentExpand: (SelectableCategoryUi) -> Unit,
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
            is CategoryPickerItemUi.ParentCategory -> TODO()
        }
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        CategoryPickerModal(
            modal = modal,
            selected = dummyCategoryUi(),
            onSelect = {}
        )
    }
}

private fun previewState() = CategoryPickerState(
    items = emptyList()
)
// endregion