package com.ivy.core.ui.color.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.color.ColorButton
import com.ivy.core.ui.color.picker.ColorPickerViewModel.Companion.COLORS_PER_ROW
import com.ivy.core.ui.color.picker.custom.HexColorPickerModal
import com.ivy.core.ui.color.picker.data.ColorSectionUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.White
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Choose
import com.ivy.design.l2_components.modal.components.Secondary
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.rememberIvyModal
import com.ivy.design.l2_components.modal.scope.ModalActionsScope
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.design.util.thenIf

private val colorItemSize = 48.dp

@Composable
fun BoxScope.ColorPickerModal(
    modal: IvyModal,
    level: Int = 1,
    initialColor: Color?,
    onColorPicked: (Color) -> Unit,
) {
    val viewModel: ColorPickerViewModel? = hiltViewModelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    var selectedColor by remember(initialColor) { mutableStateOf(initialColor) }
    val hexColorPickerModal = rememberIvyModal()

    Modal(
        modal = modal,
        level = level,
        actions = {
            ModalActions(
                modal = modal,
                hexColorPickerModal = hexColorPickerModal,
                selectedColor = selectedColor,
                onColorPicked = onColorPicked
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            item(key = "color_picker_title") {
                this@Modal.Title(text = stringResource(R.string.choose_color))
            }
            selectedColorItem(selectedColor = selectedColor) {
                // on click:
                hexColorPickerModal.show()
            }
            sections(
                sections = state.sections,
                selectedColor = selectedColor,
                onColorSelect = {
                    selectedColor = it
                    onColorPicked(it)
                    modal.hide()
                }
            )
            item(key = "color_picker_last_spacer") { SpacerVer(height = 48.dp) }
        }
    }

    HexColorPickerModal(
        modal = hexColorPickerModal,
        initialColor = selectedColor,
        onColorPicked = {
            selectedColor = it
            it.let(onColorPicked)
            modal.hide()
        }
    )
}

// region ModalActions
@Composable
private fun ModalActionsScope.ModalActions(
    modal: IvyModal,
    hexColorPickerModal: IvyModal,
    selectedColor: Color?,
    onColorPicked: (Color) -> Unit
) {
    Secondary(
        text = null,
        icon = R.drawable.outline_color_lens_24
    ) {
        hexColorPickerModal.show()
    }
    SpacerHor(width = 8.dp)
    Choose {
        selectedColor?.let(onColorPicked)
        modal.hide()
    }
}
// endregion

// region Picked Color
private fun LazyListScope.selectedColorItem(
    selectedColor: Color?,
    onClick: () -> Unit
) {
    if (selectedColor != null) {
        item(key = "selected_color_${selectedColor.value}") {
            SpacerVer(height = 24.dp)
            ColorButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                color = selectedColor,
                onClick = onClick,
            )
        }
    }
}

// endregion

// region Sections
private fun LazyListScope.sections(
    sections: List<ColorSectionUi>,
    selectedColor: Color?,
    onColorSelect: (Color) -> Unit
) {
    sections.forEach {
        section(
            section = it,
            selectedColor = selectedColor,
            onColorSelect = onColorSelect,
        )
    }
}

private fun LazyListScope.section(
    section: ColorSectionUi,
    selectedColor: Color?,
    onColorSelect: (Color) -> Unit
) {
    item(key = "section_${section.name}_${section.colorRows.size}") {
        SpacerVer(height = 24.dp)
        SectionDivider(title = section.name)
        SpacerVer(height = 12.dp)
    }
    items(
        items = section.colorRows,
        key = { "color_row_${it.first().value}" }
    ) { colorRow ->
        ColorsRow(
            colors = colorRow,
            selectedColor = selectedColor,
            onColorSelect = onColorSelect
        )
        SpacerVer(height = 12.dp)
    }
}

@Composable
private fun SectionDivider(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DividerW()
        SpacerHor(width = 16.dp)
        B1(text = title)
        SpacerHor(width = 16.dp)
        DividerW()
    }
}
// endregion

// region ColorsRow
@Composable
private fun ColorsRow(
    colors: List<Color>,
    selectedColor: Color?,
    onColorSelect: (Color) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        SpacerWeight(weight = 1f)
        for (color in colors) {
            key("color_item_${color.value}") {
                ColorItem(
                    color = color,
                    selected = selectedColor == color,
                ) {
                    // on click:
                    onColorSelect(it)
                }
                SpacerWeight(weight = 1f)
            }
        }
        MissingColorsInRowSpace(missingColors = COLORS_PER_ROW - colors.size)
    }
}

@Composable
private fun RowScope.MissingColorsInRowSpace(
    missingColors: Int
) {
    if (missingColors > 0) {
        SpacerHor(width = colorItemSize * missingColors)
        SpacerWeight(weight = 1f * missingColors)
    }
}
// endregion

// region ColorItem
@Composable
private fun ColorItem(
    color: Color,
    selected: Boolean,
    onClick: (Color) -> Unit
) {
    Shape(modifier = Modifier
        .clip(UI.shapes.circle)
        .size(colorItemSize)
        .background(color, UI.shapes.circle)
        .thenIf(selected) {
            border(
                width = 4.dp,
                color = rememberDynamicContrast(color),
                shape = UI.shapes.circle
            ).border(
                width = 5.dp,
                color = White,
                shape = UI.shapes.circle
            )
        }
        .clickable(onClick = { onClick(color) })
        .testTag("color_item_${color.value}")
    )
}
// endregion


// region Previews
@Preview
@Composable
private fun Preview() {
    IvyPreview {
        val modal = rememberIvyModal()
        modal.show()
        ColorPickerModal(
            modal = modal,
            initialColor = UI.colors.primary,
            onColorPicked = {}
        )
    }
}

private fun previewState() = ColorPickerState(
    sections = emptyList() // TODO: Add Preview state
)
// endregion