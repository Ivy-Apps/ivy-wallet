package com.ivy.core.ui.icon.picker

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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
import com.ivy.design.l0_system.color.dynamicContrast
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.l2_components.input.InputFieldType
import com.ivy.design.l2_components.input.IvyInputField
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Choose
import com.ivy.design.l2_components.modal.components.Secondary
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.l2_components.modal.scope.ModalActionsScope
import com.ivy.design.l3_ivyComponents.button.ButtonFeeling
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe
import com.ivy.design.util.thenIf

private val iconSize = IconSize.M
private val iconPadding = 12.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.IconPickerModal(
    modal: IvyModal,
    initialIcon: ItemIcon?,
    color: Color,
    onIconSelected: (ItemIconId) -> Unit
) {
    val viewModel: IconPickerViewModel? = hiltViewmodelPreviewSafe()
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
        actions = {
            ModalActions(
                searchBarVisible = searchBarVisible,
                showSearch = { searchBarVisible = true },
                resetSearch = resetSearch,
                onSelect = {
                    selectedIcon?.let(onIconSelected)
                    keyboardController?.hide()
                    modal.hide()
                }
            )
        }
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item(key = "ic_picker_title") {
                    this@Modal.Title(text = stringResource(R.string.choose_icon))
                }
                sections(
                    sections = state.sections,
                    selectedIcon = selectedIcon,
                    color = color,
                    onIconSelected = { selectedIcon = it }
                )
                item(key = "ic_picker_last_spacer") { SpacerVer(height = 48.dp) }
            }
            SearchBar(
                visible = searchBarVisible,
                query = state.searchQuery,
                resetSearch = resetSearch,
                onSearch = { viewModel?.onEvent(IconPickerEvent.Search(it)) }
            )
        }
    }
}

// region Header
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SearchBar(
    visible: Boolean,
    query: String,
    resetSearch: () -> Unit,
    onSearch: (String) -> Unit,
) {
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxWidth()
            .background(UI.colors.pure)
            .padding(top = 16.dp, bottom = 8.dp),
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current
        IvyInputField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            type = InputFieldType.SingleLine,
            value = query,
            placeholder = "Search icons...",
            imeAction = ImeAction.Search,
            onImeAction = {
                keyboardController?.hide()
                focusRequester.freeFocus()
            },
            onValueChange = { onSearch(it) },
        )

        LaunchedEffect(visible) {
            if (visible) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }
        }
        BackHandler(enabled = visible) {
            resetSearch()
        }
    }
}
// endregion

private fun LazyListScope.sections(
    sections: List<SectionUi>,
    selectedIcon: ItemIconId?,
    color: Color,
    onIconSelected: (ItemIconId) -> Unit
) {
    sections.forEach {
        section(
            section = it,
            selectedIcon = selectedIcon,
            color = color,
            onIconSelected = onIconSelected
        )
    }
}

// region Section
private fun LazyListScope.section(
    section: SectionUi,
    selectedIcon: ItemIconId?,
    color: Color,
    onIconSelected: (ItemIconId) -> Unit
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
            onIconSelected = onIconSelected
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
    onIconSelected: (ItemIconId) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpacerWeight(weight = 1f)
        for (icon in icons) {
            IconItem(
                icon = icon,
                selected = selectedIcon == icon.iconId(),
                color = color,
            ) {
                // on click:
                icon.iconId()?.let(onIconSelected)
            }
            SpacerWeight(weight = 1f)
        }
        MissingIconsSpace(missingIcons = ICONS_PER_ROW - icons.size)
    }
}

@Composable
private fun RowScope.MissingIconsSpace(
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
            .clip(CircleShape)
            .border(2.dp, if (selected) color else UI.colors.medium, CircleShape)
            .thenIf(selected) { background(color, CircleShape) }
            .clickable(onClick = onClick)
            .padding(all = iconPadding)
            .testTag(icon.iconId() ?: "no icon"),
        itemIcon = icon,
        size = iconSize,
        tint = if (selected) color.dynamicContrast() else UI.colorsInverted.medium
    )
}
// endregion


// region Modal Actions
@Composable
private fun ModalActionsScope.ModalActions(
    searchBarVisible: Boolean,
    resetSearch: () -> Unit,
    showSearch: () -> Unit,
    onSelect: () -> Unit,
) {
    Secondary(
        text = null,
        icon = if (searchBarVisible)
            R.drawable.round_search_off_24 else R.drawable.round_search_24,
        feeling = if (searchBarVisible) ButtonFeeling.Negative else ButtonFeeling.Positive
    ) {
        // toggle search bar
        if (searchBarVisible) resetSearch() else showSearch()
    }
    SpacerHor(width = 8.dp)
    Choose(onClick = onSelect)
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