package com.ivy.core.ui.category.reorder

import ReorderModal
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.uiStatePreviewSafe
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.*
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.previewModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.design.util.thenIf

@Composable
fun BoxScope.ReorderCategoriesModal(
    modal: IvyModal,
    level: Int = 1,
) {
    val viewModel: ReorderCategoriesViewModel? = hiltViewModelPreviewSafe()
    val state = uiStatePreviewSafe(viewModel, preview = ::previewState)

    ReorderModal(
        modal = modal,
        level = level,
        items = state.items,
        onReorder = {
            viewModel?.onEvent(ReorderCategoriesEvent.Reorder(it))
        }
    ) { _, item ->
        CategoryCard(category = item)
    }
}


@Composable
private fun CategoryCard(category: CategoryUi) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp) // margin top
            .thenIf(category.hasParent) {
                padding(start = 24.dp)
            }
            .padding(start = 8.dp, end = 16.dp)
            .background(category.color, UI.shapes.rounded)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contrast = rememberContrast(category.color)
        ItemIcon(itemIcon = category.icon, size = IconSize.S, tint = contrast)
        SpacerHor(width = 4.dp)
        B2(text = category.name, color = contrast)
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = previewModal()
        ReorderCategoriesModal(modal = modal)
    }
}

private fun previewState() = ReorderCategoriesStateUi(
    items = listOf(
        dummyCategoryUi("Category 1", color = Red),
        dummyCategoryUi("Category 2", color = Green),
        dummyCategoryUi("Category 3", hasParent = true),
        dummyCategoryUi("Category 4", hasParent = true, color = Green3Dark),
        dummyCategoryUi("Category 5", color = Blue),
        dummyCategoryUi("Category 6", color = Yellow),
    ),
)

// endregion