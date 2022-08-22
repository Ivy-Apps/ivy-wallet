package com.ivy.wallet.ui.theme.modal.edit

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.data.CategoryOld
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.frp.view.navigation.onScreenStart
import com.ivy.wallet.ui.theme.*
import com.ivy.wallet.ui.theme.components.ItemIconSDefaultIcon
import com.ivy.wallet.ui.theme.components.IvyBorderButton
import com.ivy.wallet.ui.theme.components.IvyCircleButton
import com.ivy.wallet.ui.theme.components.WrapContentRow
import com.ivy.wallet.ui.theme.modal.IvyModal
import com.ivy.wallet.ui.theme.modal.ModalSkip
import com.ivy.wallet.ui.theme.modal.ModalTitle
import com.ivy.wallet.utils.drawColoredShadow
import com.ivy.wallet.utils.hideKeyboard
import com.ivy.wallet.utils.thenIf
import java.util.*

@ExperimentalFoundationApi
@Composable
fun BoxWithConstraintsScope.ChooseCategoryModal(
    id: UUID = UUID.randomUUID(),
    visible: Boolean,
    initialCategory: CategoryOld?,
    categories: List<CategoryOld>,

    showCategoryModal: (CategoryOld?) -> Unit,
    onCategoryChanged: (CategoryOld?) -> Unit,
    dismiss: () -> Unit
) {
    var selectedCategory by remember(initialCategory) {
        mutableStateOf(initialCategory)
    }

    IvyModal(
        id = id,
        visible = visible,
        dismiss = dismiss,
        PrimaryAction = {
            ModalSkip {
                save(
                    category = selectedCategory,
                    onCategoryChanged = onCategoryChanged,
                    dismiss = dismiss
                )
            }
        }
    ) {
        val view = LocalView.current
        onScreenStart {
            hideKeyboard(view)
        }

        Spacer(Modifier.height(32.dp))

        ModalTitle(
            text = stringResource(R.string.choose_category)
        )

        Spacer(Modifier.height(24.dp))

        CategoryPicker(
            categories = categories,
            selectedCategory = selectedCategory,
            showCategoryModal = showCategoryModal,
            onEditCategory = {
                showCategoryModal(it)
            }
        ) {
            selectedCategory = it
            save(
                shouldDismissModal = it != null,
                category = it,
                onCategoryChanged = onCategoryChanged,
                dismiss = dismiss
            )
        }

        Spacer(Modifier.height(56.dp))
    }
}

private fun save(
    shouldDismissModal: Boolean = true,

    category: CategoryOld?,
    onCategoryChanged: (CategoryOld?) -> Unit,
    dismiss: () -> Unit
) {
    onCategoryChanged(category)
    if (shouldDismissModal) {
        dismiss()
    }
}

@ExperimentalFoundationApi
@Composable
private fun CategoryPicker(
    categories: List<CategoryOld>,
    selectedCategory: CategoryOld?,
    showCategoryModal: (CategoryOld?) -> Unit,
    onEditCategory: (CategoryOld) -> Unit,
    onSelected: (CategoryOld?) -> Unit,
) {
    val data = mutableListOf<Any>()
    data.addAll(categories)
    data.add(AddNewCategory())

    WrapContentRow(
        modifier = Modifier
            .padding(horizontal = 16.dp),
        horizontalMarginBetweenItems = 12.dp,
        verticalMarginBetweenRows = 12.dp,
        items = data
    ) {
        when (it) {
            is CategoryOld -> {
                CategoryButton(
                    category = it,
                    selected = it == selectedCategory,
                    onClick = {
                        onSelected(it)
                    },
                    onLongClick = {
                        onEditCategory(it)
                    },
                    onDeselect = {
                        onSelected(null)
                    }
                )
            }
            is AddNewCategory -> {
                AddNewButton {
                    showCategoryModal(null)
                }
            }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun CategoryButton(
    category: CategoryOld,
    selected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDeselect: () -> Unit,
) {
    val categoryColor = category.color.toComposeColor()

    Row(
        modifier = Modifier
            .thenIf(selected) {
                drawColoredShadow(categoryColor)
            }
            .clip(UI.shapes.rFull)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .border(
                width = 2.dp,
                color = if (selected) UI.colors.pureInverse else UI.colors.medium,
                shape = UI.shapes.rFull
            )
            .thenIf(selected) {
                background(categoryColor, UI.shapes.rFull)
            }
            .testTag("choose_category_button"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.width(if (selected) 12.dp else 8.dp))

        ItemIconSDefaultIcon(
            modifier = Modifier
                .background(categoryColor, CircleShape),
            iconName = category.icon,
            defaultIcon = R.drawable.ic_custom_category_s,
            tint = findContrastTextColor(categoryColor)
        )

        Text(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .padding(
                    start = if (selected) 12.dp else 12.dp,
                    end = if (selected) 20.dp else 24.dp
                ),
            text = category.name,
            style = UI.typo.b2.style(
                color = if (selected)
                    findContrastTextColor(categoryColor) else UI.colors.pureInverse,
                fontWeight = FontWeight.SemiBold
            )
        )

        if (selected) {

            val deselectBtnBackground = findContrastTextColor(categoryColor)
            IvyCircleButton(
                modifier = Modifier
                    .size(32.dp),
                icon = R.drawable.ic_remove,
                backgroundGradient = Gradient.solid(deselectBtnBackground),
                tint = findContrastTextColor(deselectBtnBackground)
            ) {
                onDeselect()
            }

            Spacer(Modifier.width(8.dp))
        }
    }
}

@Composable
fun AddNewButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    IvyBorderButton(
        modifier = modifier,
        text = stringResource(R.string.add_new),
        backgroundGradient = Gradient.solid(UI.colors.mediumInverse),
        iconStart = R.drawable.ic_plus,
        textStyle = UI.typo.b2.style(
            color = UI.colors.pureInverse,
            fontWeight = FontWeight.Bold
        ),
        iconTint = UI.colors.pureInverse,
        padding = 10.dp,
        onClick = onClick
    )
}

private class AddNewCategory

@ExperimentalFoundationApi
@Preview
@Composable
private fun PreviewChooseCategoryModal() {
    com.ivy.core.ui.temp.Preview {
        val categories = mutableListOf(
            CategoryOld("Test", color = Ivy.toArgb()),
            CategoryOld("Second", color = Orange.toArgb()),
            CategoryOld("Third", color = Red.toArgb()),
        )

        ChooseCategoryModal(
            visible = true,
            initialCategory = categories.first(),
            categories = categories,
            showCategoryModal = { },
            onCategoryChanged = { }
        ) {

        }
    }
}