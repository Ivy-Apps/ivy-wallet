package com.ivy.core.ui.icon.picker

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.data.icon.iconId
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.icon.picker.data.SectionUi
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
import com.ivy.design.l2_components.modal.scope.ModalScope
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe
import com.ivy.design.util.thenIf

@Composable
fun BoxScope.IconPickerModal(
    modal: IvyModal,
    initialIcon: ItemIcon?,
    color: Color,
    onIconSelected: (ItemIcon) -> Unit
) {
    val viewModel: IconPickerViewModel? = hiltViewmodelPreviewSafe()
    val state = viewModel?.uiState?.collectAsState()?.value ?: previewState()

    var selectedIcon by remember(initialIcon) { mutableStateOf(initialIcon) }
    var searchBarVisible by remember(initialIcon, color) { mutableStateOf(false) }

    Modal(
        modal = modal,
        actions = {
            Secondary(
                text = null,
                icon = if (searchBarVisible)
                    R.drawable.ic_round_close_24 else R.drawable.round_search_24
            ) {
                // toggle search bar
                searchBarVisible = !searchBarVisible
            }
            SpacerHor(width = 8.dp)
            Choose {
                selectedIcon?.let(onIconSelected)
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            header(
                modal = this@Modal,
                searchBarVisible = searchBarVisible,
                searchQuery = state.searchQuery,
                onEvent = { viewModel?.onEvent(it) }
            )
            sections(
                sections = state.sections,
                selectedIcon = null,
                color = color,
                onIconSelected = { selectedIcon = it }
            )
            item(key = "ic_picker_last_spacer") { SpacerVer(height = 48.dp) }
        }
    }
}

// region Header
@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.header(
    modal: ModalScope,
    searchBarVisible: Boolean,
    searchQuery: String,
    onEvent: (IconPickerEvent) -> Unit
) {
    stickyHeader(
        key = "ic_picker_header"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(UI.colors.pure)
                .padding(bottom = 4.dp)
        ) {
            modal.Title(text = stringResource(R.string.choose_icon))
            SearchBar(
                visible = searchBarVisible,
                query = searchQuery,
                onSearch = { onEvent(IconPickerEvent.Search(it)) }
            )
        }
    }
}

@Composable
private fun SearchBar(
    visible: Boolean,
    query: String,
    onSearch: (String) -> Unit,
) {
    AnimatedVisibility(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        visible = visible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        IvyInputField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            type = InputFieldType.SingleLine,
            value = TextFieldValue(query),
            placeholder = "Search",
            onValueChange = { onSearch(it.text) }
        )
    }
}
// endregion

private fun LazyListScope.sections(
    sections: List<SectionUi>,
    selectedIcon: ItemIcon?,
    color: Color,
    onIconSelected: (ItemIcon) -> Unit
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
    selectedIcon: ItemIcon?,
    color: Color,
    onIconSelected: (ItemIcon) -> Unit
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
    selectedIcon: ItemIcon?,
    color: Color,
    onIconSelected: (ItemIcon) -> Unit
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
                selected = selectedIcon == icon,
                color = color,
            ) {
                onIconSelected(icon)
            }
            SpacerWeight(weight = 1f)
        }
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
            .padding(all = 8.dp)
            .testTag(icon.iconId() ?: "no icon"),
        icon = icon,
        size = IconSize.S,
        tint = if (selected) color.dynamicContrast() else UI.colorsInverted.medium
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