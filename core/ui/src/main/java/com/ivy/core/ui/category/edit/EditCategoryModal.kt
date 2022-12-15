package com.ivy.core.ui.category.edit

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.category.BaseCategoryModal
import com.ivy.core.ui.category.edit.component.DeleteCategoryModal
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.data.category.CategoryType
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l3_ivyComponents.button.ArchiveButton
import com.ivy.design.l3_ivyComponents.button.DeleteButton
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.EditCategoryModal(
    modal: IvyModal,
    categoryId: String,
    level: Int = 1,
) {
    val viewModel: EditCategoryViewModel? = hiltViewModelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    LaunchedEffect(categoryId) {
        viewModel?.onEvent(EditCategoryEvent.Initial(categoryId))
    }

    val deleteModal = rememberIvyModal()

    val keyboardController = LocalSoftwareKeyboardController.current
    BaseCategoryModal(
        modal = modal,
        level = level,
        autoFocusNameInput = false,
        title = stringResource(R.string.edit_category),
        nameInputHint = stringResource(R.string.category_name),
        positiveActionText = stringResource(R.string.save),
        secondaryActions = {
            ArchiveButton(
                archived = state.archived,
                color = state.color,
                onArchive = {
                    keyboardController?.hide()
                    modal.hide()
                    viewModel?.onEvent(EditCategoryEvent.Archive)
                },
                onUnarchive = {
                    keyboardController?.hide()
                    modal.hide()
                    viewModel?.onEvent(EditCategoryEvent.Unarchive)
                }
            )
            SpacerHor(width = 8.dp)
            DeleteButton {
                keyboardController?.hide()
                deleteModal.show()
            }
            SpacerHor(width = 12.dp)
        },
        icon = state.icon,
        initialName = state.initialName,
        color = state.color,
        parent = state.parent,
        type = state.type,
        onNameChange = { viewModel?.onEvent(EditCategoryEvent.NameChange(it)) },
        onIconChange = { viewModel?.onEvent(EditCategoryEvent.IconChange(it)) },
        onColorChange = { viewModel?.onEvent(EditCategoryEvent.ColorChange(it)) },
        onTypeChange = { viewModel?.onEvent(EditCategoryEvent.TypeChange(it)) },
        onParentCategoryChange = { viewModel?.onEvent(EditCategoryEvent.ParentChange(it)) },
        onSave = { viewModel?.onEvent(EditCategoryEvent.EditCategory) }
    )

    DeleteCategoryModal(
        modal = deleteModal,
        level = level + 1,
        categoryName = state.initialName,
        archived = state.archived,
        onArchive = {
            keyboardController?.hide()
            modal.hide()
            viewModel?.onEvent(EditCategoryEvent.Archive)
        },
        onDelete = {
            keyboardController?.hide()
            modal.hide()
            viewModel?.onEvent(EditCategoryEvent.Delete)
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
        EditCategoryModal(
            modal = modal,
            categoryId = ""
        )
    }
}

private fun previewState() = EditCategoryState(
    categoryId = "",
    icon = dummyIconSized(R.drawable.ic_custom_category_m),
    initialName = "Category",
    parent = null,
    color = Purple,
    archived = false,
    type = CategoryType.Both,
)
// endregion