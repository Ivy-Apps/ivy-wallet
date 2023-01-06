package com.ivy.core.ui.category.create

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.R
import com.ivy.core.ui.category.BaseCategoryModal
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.data.category.CategoryType
import com.ivy.design.l0_system.UI
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@Composable
fun BoxScope.CreateCategoryModal(
    modal: IvyModal,
    level: Int = 1
) {
    val viewModel: CreateCategoryViewModel? = hiltViewModelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    val primary = UI.colors.primary
    var color by remember(primary) { mutableStateOf(primary) }
    var parent by remember { mutableStateOf<CategoryUi?>(null) }
    var type by remember { mutableStateOf(CategoryType.Both) }

    val newCategoryText = "New Category"
    BaseCategoryModal(
        modal = modal,
        level = level,
        autoFocusNameInput = true,
        title = newCategoryText,
        nameInputHint = newCategoryText,
        positiveActionText = stringResource(R.string.add_category),
        icon = state.icon,
        initialName = "",
        color = color,
        parent = parent,
        type = type,
        onNameChange = { viewModel?.onEvent(CreateCategoryEvent.NameChange(it)) },
        onIconChange = { viewModel?.onEvent(CreateCategoryEvent.IconChange(it)) },
        onParentCategoryChange = { parent = it },
        onTypeChange = { type = it },
        onColorChange = { color = it },
        onSave = {
            viewModel?.onEvent(
                CreateCategoryEvent.CreateCategory(
                    color = it.color,
                    parent = it.parent
                )
            )
        }
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        CreateCategoryModal(modal = modal)
    }
}

private fun previewState() = CreateCategoryState(
    icon = dummyIconSized(R.drawable.ic_custom_category_m)
)
// endregion
