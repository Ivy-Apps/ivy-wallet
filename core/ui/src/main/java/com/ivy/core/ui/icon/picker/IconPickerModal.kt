package com.ivy.core.ui.icon.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.data.icon.iconId
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.icon.picker.IconPickerViewModel.Companion.ICONS_PER_ROW
import com.ivy.core.ui.icon.picker.data.SectionUi
import com.ivy.core.ui.icon.toDp
import com.ivy.data.ItemIconId
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Choose
import com.ivy.design.l2_components.modal.components.Search
import com.ivy.design.l2_components.modal.components.SearchButton
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewModelPreviewSafe
import com.ivy.design.util.thenIf

private val iconSize = IconSize.M
private val iconPadding = 12.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.IconPickerModal(
    modal: IvyModal,
    level: Int = 1,
    initialIcon: ItemIcon?,
    color: Color,
    onIconPick: (ItemIconId) -> Unit
) {
    val viewModel: IconPickerViewModel? = hiltViewModelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    var selectedIcon by remember(initialIcon) { mutableStateOf(initialIcon?.iconId()) }
    var searchBarVisible by remember(initialIcon, color) { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val resetSearch = {
        keyboardController?.hide()
        viewModel?.onEvent(IconPickerEvent.Search(query = ""))
        searchBarVisible = false
    }

    Modal(
        modal = modal,
        level = level,
        actions = {
            SearchButton(searchBarVisible = searchBarVisible) {
                if (searchBarVisible) resetSearch() else searchBarVisible = true
            }
            SpacerHor(width = 8.dp)
            Choose {
                selectedIcon?.let(onIconPick)
                keyboardController?.hide()
                modal.hide()
            }
        }
    ) {
        Search(
            searchBarVisible = searchBarVisible,
            initialSearchQuery = state.searchQuery,
            searchHint = "Search by words (car, home, tech)",
            resetSearch = resetSearch,
            onSearch = { viewModel?.onEvent(IconPickerEvent.Search(it)) },
        ) {
            item(key = "ic_picker_title") {
                this@Modal.Title(text = stringResource(R.string.choose_icon))
            }
            sections(
                sections = state.sections,
                selectedIcon = selectedIcon,
                color = color,
                onIconSelect = {
                    selectedIcon = it
                    onIconPick(it)
                    keyboardController?.hide()
                    modal.hide()
                }
            )
            item(key = "ic_picker_last_spacer") { SpacerVer(height = 48.dp) }
        }
    }
}

private fun LazyListScope.sections(
    sections: List<SectionUi>,
    selectedIcon: ItemIconId?,
    color: Color,
    onIconSelect: (ItemIconId) -> Unit
) {
    sections.forEach {
        section(
            section = it,
            selectedIcon = selectedIcon,
            color = color,
            onIconSelect = onIconSelect
        )
    }
}

// region Section
private fun LazyListScope.section(
    section: SectionUi,
    selectedIcon: ItemIconId?,
    color: Color,
    onIconSelect: (ItemIconId) -> Unit
) {
    item(key = "section_${section.name}_${section.iconRows.size}") {
        SpacerVer(height = 24.dp)
        SectionDivider(title = section.name)
        SpacerVer(height = 12.dp)
    }
    items(
        items = section.iconRows,
        key = { "ic_row_${it.first().iconId()}" }
    ) { iconRow ->
        IconsRow(
            icons = iconRow,
            selectedIcon = selectedIcon,
            color = color,
            onIconSelect = onIconSelect
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

// region Icons row
@Composable
private fun IconsRow(
    icons: List<ItemIcon>,
    selectedIcon: ItemIconId?,
    color: Color,
    onIconSelect: (ItemIconId) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        SpacerWeight(weight = 1f)
        for (icon in icons) {
            val iconId = icon.iconId()
            key("ic_item_$iconId") {
                IconItem(
                    icon = icon,
                    selected = selectedIcon == iconId,
                    color = color,
                ) {
                    // on click:
                    iconId?.let(onIconSelect)
                }
                SpacerWeight(weight = 1f)
            }
        }
        MissingIconsInRowSpace(missingIcons = ICONS_PER_ROW - icons.size)
    }
}

@Composable
private fun RowScope.MissingIconsInRowSpace(
    missingIcons: Int
) {
    if (missingIcons > 0) {
        val iconSize = iconSize.toDp() + (iconPadding * missingIcons)
        SpacerHor(width = iconSize * missingIcons)
        SpacerWeight(weight = 1f * missingIcons)
    }
}

@Composable
private fun IconItem(
    icon: ItemIcon,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    ItemIcon(
        modifier = Modifier
            .clip(UI.shapes.circle)
            .border(2.dp, if (selected) color else UI.colors.medium, UI.shapes.circle)
            .thenIf(selected) { background(color, UI.shapes.circle) }
            .clickable(onClick = onClick)
            .padding(all = iconPadding)
            .testTag(icon.iconId() ?: "no icon"),
        itemIcon = icon,
        size = iconSize,
        tint = if (selected) rememberDynamicContrast(color) else UI.colorsInverted.medium
    )
}
// endregion


// region Preview
@Preview
@Composable
private fun Preview() {
    IvyPreview {

    }
}

private fun previewState() = IconPickerStateUi(
    sections = emptyList(), // TODO: Provide preview state
    searchQuery = ""
)
// endregion