package com.ivy.core.ui.category

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.category.component.CategoryTypeSection
import com.ivy.core.ui.category.component.ParentCategoryButton
import com.ivy.core.ui.category.pickparent.ParentCategoryPickerModal
import com.ivy.core.ui.color.ColorButton
import com.ivy.core.ui.color.picker.ColorPickerModal
import com.ivy.core.ui.component.ItemIconNameRow
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.core.ui.icon.picker.IconPickerModal
import com.ivy.data.ItemIconId
import com.ivy.data.category.CategoryType
import com.ivy.design.l0_system.UI
import com.ivy.design.l1_buildingBlocks.DividerHor
import com.ivy.design.l1_buildingBlocks.SpacerVer
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Positive
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l2_components.modal.scope.ModalActionsScope
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.util.IvyPreview

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun BoxScope.BaseCategoryModal(
    modal: IvyModal,
    level: Int,
    autoFocusNameInput: Boolean,
    title: String,
    nameInputHint: String,
    positiveActionText: String,
    icon: ItemIcon,
    initialName: String,
    color: Color,
    parent: CategoryUi?,
    type: CategoryType,
    secondaryActions: (@Composable ModalActionsScope.() -> Unit)? = null,
    contentBelow: (LazyListScope.() -> Unit)? = null,
    onIconChange: (ItemIconId) -> Unit,
    onNameChange: (String) -> Unit,
    onColorChange: (Color) -> Unit,
    onParentCategoryChange: (CategoryUi?) -> Unit,
    onTypeChange: (CategoryType) -> Unit,
    onSave: (SaveCategoryInfo) -> Unit,
) {
    val iconPickerModal = rememberIvyModal()
    val colorPickerModal = rememberIvyModal()
    val chooseParentModal = rememberIvyModal()

    val keyboardController = LocalSoftwareKeyboardController.current
    Modal(
        modal = modal,
        level = level,
        actions = {
            secondaryActions?.invoke(this)
            Positive(
                text = positiveActionText,
                feeling = Feeling.Custom(color)
            ) {
                onSave(
                    SaveCategoryInfo(
                        color = color,
                        parent = parent,
                    )
                )
                keyboardController?.hide()
                modal.hide()
            }
        }
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            item(key = "modal_title") {
                Title(text = title)
                SpacerVer(height = 24.dp)
            }
            item(key = "icon_name_color") {
                // Keep in one item because so the title
                // won't disappear on scroll
                ItemIconNameRow(
                    icon = icon,
                    color = color,
                    initialName = initialName,
                    nameInputHint = nameInputHint,
                    autoFocusInput = autoFocusNameInput,
                    onPickIcon = {
                        keyboardController?.hide()
                        iconPickerModal.show()
                    },
                    onNameChange = onNameChange,
                )
                SpacerVer(height = 16.dp)
                ColorButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = color
                ) {
                    keyboardController?.hide()
                    colorPickerModal.show()
                }
                SpacerVer(height = 16.dp)
            }
            item(key = "parent_category") {
                ParentCategoryButton(
                    parent = parent,
                    color = color,
                ) {
                    keyboardController?.hide()
                    chooseParentModal.show()
                }
            }
            item(key = "line_divider") {
                SpacerVer(height = 24.dp)
                DividerHor()
                SpacerVer(height = 12.dp)
            }
            item(key = "category_type") {
                CategoryTypeSection(
                    type = type,
                    onSelect = onTypeChange
                )
            }
            contentBelow?.invoke(this)
            item(key = "last_item_spacer") {
                SpacerVer(height = 48.dp) // last spacer
            }
        }
    }

    IconPickerModal(
        modal = iconPickerModal,
        level = level + 1,
        initialIcon = icon,
        color = color,
        onIconPick = onIconChange,
    )
    ColorPickerModal(
        modal = colorPickerModal,
        level = level + 1,
        initialColor = color,
        onColorPicked = onColorChange,
    )
    ParentCategoryPickerModal(
        modal = chooseParentModal,
        level = level + 1,
        selected = parent,
        onPick = onParentCategoryChange
    )
}

data class SaveCategoryInfo(
    val color: Color,
    val parent: CategoryUi?
)


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        BaseCategoryModal(
            modal = modal,
            level = 1,
            autoFocusNameInput = false,
            title = stringResource(R.string.edit_category),
            nameInputHint = stringResource(R.string.category_name),
            positiveActionText = stringResource(R.string.save),
            icon = dummyIconSized(R.drawable.ic_custom_category_m),
            color = UI.colors.primary,
            initialName = "Category",
            parent = null,
            type = CategoryType.Both,
            onNameChange = {},
            onIconChange = {},
            onSave = {},
            onColorChange = {},
            onParentCategoryChange = {},
            onTypeChange = {},
        )
    }
}
// endregion