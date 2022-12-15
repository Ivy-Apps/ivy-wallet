package com.ivy.core.ui.category.pick

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.category.create.CreateCategoryModal
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.*
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Negative
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.design.util.thenWhen

@Composable
fun BoxScope.CategoryPickerModal(
    modal: IvyModal,
    selected: CategoryUi?,
    level: Int = 1,
    onPick: (CategoryUi?) -> Unit,
) {
    val viewModel: CategoryPickerViewModel? = hiltViewModelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value
        ?: previewState()

    val createCategoryModal = rememberIvyModal()

    Modal(
        modal = modal,
        level = level,
        actions = {
            Negative(text = "Remove parent") {
                onPick(null)
                modal.hide()
            }
        }
    ) {
        LazyColumn {
            item {
                Title(text = "Choose parent")
            }
            categoryItems(
                items = state.categories,
                selected = selected,
                onSelect = {
                    onPick(it)
                    modal.hide()
                }
            )
            createFolderItem(
                onCreateFolder = { createCategoryModal.show() }
            )
            item {
                SpacerVer(height = 48.dp) // last item spacer
            }
        }
    }

    CreateCategoryModal(modal = createCategoryModal)
}

// region Folders
private fun LazyListScope.categoryItems(
    items: List<CategoryUi>,
    selected: CategoryUi?,
    onSelect: (CategoryUi) -> Unit
) {
    this.items(
        items = items,
        key = { "category_${it.id}" }
    ) { category ->
        SpacerVer(height = 12.dp)
        CategoryItem(
            category = category,
            selected = category.id == selected?.id
        ) {
            onSelect(category)
        }
    }
}

@Composable
internal fun CategoryItem(
    category: CategoryUi,
    selected: Boolean,
    onClick: () -> Unit
) {
    val dynamicContrast = rememberDynamicContrast(category.color)
    val contrastColor = rememberContrast(category.color)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(UI.shapes.squared)
            .thenWhen {
                when (selected) {
                    true -> background(category.color, UI.shapes.squared)
                        .border(2.dp, dynamicContrast, UI.shapes.squared)
                    false -> border(2.dp, dynamicContrast, UI.shapes.squared)
                }
            }
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val color = if (selected) contrastColor else UI.colorsInverted.pure
        ItemIcon(
            itemIcon = category.icon,
            size = IconSize.S,
            tint = color,
        )
        SpacerHor(width = 8.dp)
        B2(text = category.name, color = color)
    }
}
// endregion

// region Add folder
fun LazyListScope.createFolderItem(
    onCreateFolder: () -> Unit,
) {
    item(key = "add_folder_item") {
        SpacerVer(height = 12.dp)
        IvyButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            size = ButtonSize.Big,
            visibility = Visibility.Medium,
            feeling = Feeling.Positive,
            text = "New category",
            icon = R.drawable.ic_round_add_24,
            onClick = onCreateFolder,
        )
    }
}
// endregion

// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        CategoryPickerModal(
            modal = modal,
            selected = dummyCategoryUi(id = "selected"),
            onPick = {}
        )
    }
}

private fun previewState() = CategoryPickerState(
    categories = listOf(
        dummyCategoryUi(id = "selected", name = "Category 1", color = Green),
        dummyCategoryUi(name = "Category 2", color = Yellow),
        dummyCategoryUi(name = "Category 3", color = Purple),
    )
)
// endregion