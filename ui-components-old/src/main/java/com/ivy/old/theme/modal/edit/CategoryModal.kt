package com.ivy.wallet.ui.theme.modal.edit

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.CategoryOld
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.util.IvyPreview
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.ui.category.CategoryList
import com.ivy.wallet.ui.theme.Ivy
import com.ivy.wallet.ui.theme.components.ItemIconMDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyCheckboxWithText
import com.ivy.wallet.ui.theme.components.IvyColorPicker
import com.ivy.wallet.ui.theme.components.IvyNameTextField
import com.ivy.wallet.ui.theme.dynamicContrast
import com.ivy.wallet.ui.theme.modal.ChooseIconModal
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalAddSave
import com.ivy.wallet.ui.theme.modal.ModalTitle
import com.ivy.wallet.utils.hideKeyboard
import com.ivy.wallet.utils.isNotNullOrBlank
import com.ivy.wallet.utils.selectEndTextFieldValue
import java.util.*

data class CategoryModalData(
    val category: CategoryOld?,
    val id: UUID = UUID.randomUUID(),
    val autoFocusKeyboard: Boolean = true
)

@Composable
fun BoxWithConstraintsScope.CategoryModal(
    modal: CategoryModalData?,
    isCategoryParentCategory: Boolean = true,
    parentCategoryList: List<CategoryOld> = emptyList(),
    onCreateCategory: (CreateCategoryData) -> Unit,
    onEditCategory: (CategoryOld) -> Unit,
    dismiss: () -> Unit,
) {
    val initialCategory = modal?.category
    var nameTextFieldValue by remember(modal) {
        mutableStateOf(selectEndTextFieldValue(initialCategory?.name))
    }
    var color by remember(modal) {
        mutableStateOf(initialCategory?.color?.let { Color(it) } ?: Ivy)
    }
    var icon by remember(modal) {
        mutableStateOf(initialCategory?.icon)
    }
    var chooseIconModalVisible by remember(modal) {
        mutableStateOf(false)
    }

    var isSubCategory by remember(modal) {
        mutableStateOf(modal?.category?.parentCategoryId != null)
    }

    var selectedParentCategory: CategoryOld? by remember(modal) {
        mutableStateOf(parentCategoryList.find { it.id == modal?.category?.parentCategoryId })
    }
    val isParentCat: Boolean by remember(modal) {
        mutableStateOf(if (initialCategory == null) false else isCategoryParentCategory)
    }

    IvyModal(
        id = modal?.id,
        visible = modal != null,
        dismiss = dismiss,
        PrimaryAction = {
            ModalAddSave(
                item = modal?.category,
                enabled = nameTextFieldValue.text.isNotNullOrBlank()
                        && ((isSubCategory && selectedParentCategory != null) || !isSubCategory)
            ) {
                if (initialCategory != null) {
                    onEditCategory(
                        initialCategory.copy(
                            name = nameTextFieldValue.text.trim(),
                            color = color.toArgb(),
                            icon = icon,
                            parentCategoryId = selectedParentCategory?.id
                        )
                    )
                } else {
                    onCreateCategory(
                        CreateCategoryData(
                            name = nameTextFieldValue.text.trim(),
                            color = color,
                            icon = icon,
                            parentCategory = selectedParentCategory
                        )
                    )
                }

                dismiss()
            }
        }
    ) {
        Spacer(Modifier.height(32.dp))

        ModalTitle(
            text = if (modal?.category != null) stringResource(R.string.edit_category)
            else stringResource(
                R.string.create_category
            )
        )

        Spacer(Modifier.height(24.dp))

        IconNameRow(
            hint = stringResource(R.string.category_name),
            defaultIcon = R.drawable.ic_custom_category_m,
            color = color,
            icon = icon,

            autoFocusKeyboard = modal?.autoFocusKeyboard ?: true,

            nameTextFieldValue = nameTextFieldValue,
            setNameTextFieldValue = { nameTextFieldValue = it },
            showChooseIconModal = {
                chooseIconModalVisible = true
            }
        )

        Spacer(Modifier.height(40.dp))

        IvyColorPicker(
            selectedColor = color,
            onColorSelected = { color = it }
        )

        if (isSubCategory) {
            Text(
                modifier = Modifier.padding(top = 16.dp, end = 32.dp, start = 32.dp),
                text = stringResource(R.string.parent_category),
                style = UI.typo.b2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            CategoryList(
                categoryList = parentCategoryList,
                selectedCategory = selectedParentCategory
            ) {
                selectedParentCategory = it
            }
        }

        if (!isParentCat && parentCategoryList.isNotEmpty()) {
            IvyCheckboxWithText(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 0.dp),
                text = stringResource(R.string.mark_as_sub_category),
                checked = isSubCategory
            ) {
                isSubCategory = it
                if (!isSubCategory)
                    selectedParentCategory =
                        null // Reset Sub-Category if Sub-Category Option is Unchecked
            }
        }
        if (parentCategoryList.isNotEmpty() && isParentCat) {
            Text(
                modifier = Modifier.padding(top = 32.dp, start = 32.dp),
                text = stringResource(R.string.marked_parent_category),
                style = UI.typo.nB2.style(
                    color = UI.colors.pureInverse,
                    fontWeight = FontWeight.Normal
                )
            )
        }

        Spacer(Modifier.height(16.dp))
    }

    ChooseIconModal(
        visible = chooseIconModalVisible,
        initialIcon = icon ?: "category",
        color = color,
        dismiss = { chooseIconModalVisible = false }
    ) {
        icon = it
    }
}


@Composable
fun IconNameRow(
    hint: String,
    @DrawableRes defaultIcon: Int,
    color: Color,
    icon: String?,

    autoFocusKeyboard: Boolean,

    nameTextFieldValue: TextFieldValue,
    setNameTextFieldValue: (TextFieldValue) -> Unit,

    showChooseIconModal: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        val nameFocus = FocusRequester()

        onScreenStart {
            if (autoFocusKeyboard) {
                nameFocus.requestFocus()
            }
        }

        Spacer(Modifier.width(24.dp))

        ItemIconMDefaultIcon(
            modifier = Modifier
                .clip(CircleShape)
                .background(color, CircleShape)
                .clickable {
                    showChooseIconModal()
                }
                .testTag("modal_item_icon"),
            iconName = icon,
            tint = color.dynamicContrast(),
            defaultIcon = defaultIcon
        )

        val view = LocalView.current
        IvyNameTextField(
            modifier = Modifier
                .padding(start = 28.dp, end = 36.dp)
                .focusRequester(nameFocus),
            underlineModifier = Modifier.padding(start = 24.dp, end = 32.dp),
            value = nameTextFieldValue,
            hint = hint,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
                autoCorrect = true
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    hideKeyboard(view)
                }
            ),
        ) { newValue ->
            setNameTextFieldValue(newValue)
        }
    }
}

@Preview
@Composable
private fun PreviewCategoryModal() {
    IvyPreview {
        CategoryModal(
            modal = CategoryModalData(null),
            onCreateCategory = { },
            onEditCategory = { }
        ) {

        }
    }
}