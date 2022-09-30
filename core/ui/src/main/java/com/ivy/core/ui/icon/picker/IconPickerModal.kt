package com.ivy.core.ui.icon.picker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
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
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.data.icon.iconId
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.core.ui.icon.picker.data.PickerItemUi
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.dynamicContrast
import com.ivy.design.l1_buildingBlocks.*
import com.ivy.design.l2_components.modal.IvyModal
import com.ivy.design.l2_components.modal.Modal
import com.ivy.design.l2_components.modal.components.Choose
import com.ivy.design.l2_components.modal.components.Secondary
import com.ivy.design.l2_components.modal.components.Title
import com.ivy.design.util.IvyPreview
import com.ivy.design.util.hiltViewmodelPreviewSafe
import com.ivy.design.util.thenIf

private const val ICONS_PER_ROW = 5

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxScope.IconPickerModal(
    modal: IvyModal,
    initialIcon: ItemIcon,
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
                // toggle search
                searchBarVisible = !searchBarVisible
            }
            SpacerHor(width = 8.dp)
            Choose {
                onIconSelected(selectedIcon)
            }
        }
    ) {
        LazyColumn {
            stickyHeader {
                Title(text = stringResource(R.string.choose_icon))
                SearchBar(
                    visible = searchBarVisible,
                    query = state.searchQuery,
                    onSearch = {
                        viewModel?.onEvent(IconPickerEvent.Search(it))
                    }
                )
            }

            pickerItems(
                items = state.items,
                selectedIcon = null,
                color = color,
                onIconSelected = { selectedIcon = it }
            )

            item { SpacerVer(height = 48.dp) }
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
        visible = visible
    ) {

    }
}

private fun LazyListScope.pickerItems(
    items: List<PickerItemUi>,
    selectedIcon: ItemIcon?,
    color: Color,
    onIconSelected: (ItemIcon) -> Unit
) {
    groupIcons(
        items = items,
        selectedIcon = selectedIcon,
        color = color,
        onIconSelected = onIconSelected
    )
}

// region Group icons by sections and rows
private tailrec fun LazyListScope.groupIcons(
    items: List<PickerItemUi>,
    iconsRow: List<PickerItemUi.Icon> = emptyList(),
    selectedIcon: ItemIcon?,
    color: Color,
    onIconSelected: (ItemIcon) -> Unit
) {
    if (items.isNotEmpty()) {
        //recurse
        when (val currentItem = items.first()) {
            is PickerItemUi.Section -> {
                addIconsRowIfNotEmpty(
                    iconsRow = iconsRow,
                    selectedIcon = selectedIcon,
                    color = color,
                    onIconSelected = onIconSelected
                )
                item { SectionDivider(title = currentItem.name) }

                //RECURSE
                groupIcons(
                    items = items.drop(1),
                    iconsRow = emptyList(),
                    selectedIcon = selectedIcon,
                    color = color,
                    onIconSelected = onIconSelected

                )
            }
            is PickerItemUi.Icon -> {
                //icon
                if (iconsRow.size == ICONS_PER_ROW) {
                    // maximum icons per row reached
                    // reset accumulator and recurse
                    addIconsRowIfNotEmpty(
                        iconsRow = iconsRow,
                        selectedIcon = selectedIcon,
                        color = color,
                        onIconSelected = onIconSelected
                    )

                    //RECURSE
                    groupIcons(
                        items = items.drop(1),
                        iconsRow = emptyList(),
                        selectedIcon = selectedIcon,
                        color = color,
                        onIconSelected = onIconSelected

                    )
                } else {
                    // maximum icons per row not reached, continue

                    //RECURSE
                    groupIcons(
                        items = items.drop(1),
                        iconsRow = iconsRow + currentItem,

                        selectedIcon = selectedIcon,
                        color = color,
                        onIconSelected = onIconSelected

                    )
                }
            }
        }
    } else {
        //end recursion
        addIconsRowIfNotEmpty(
            iconsRow = iconsRow,
            selectedIcon = selectedIcon,
            color = color,
            onIconSelected = onIconSelected
        )
    }
}

private fun LazyListScope.addIconsRowIfNotEmpty(
    iconsRow: List<PickerItemUi.Icon>,
    selectedIcon: ItemIcon?,
    color: Color,
    onIconSelected: (ItemIcon) -> Unit
) {
    if (iconsRow.isNotEmpty()) {
        item {
            IconsRow(
                itemIcons = iconsRow,
                selectedIcon = selectedIcon,
                color = color,
                onIconSelected = onIconSelected
            )
            SpacerVer(height = 16.dp)
        }
    }
}
// endregion

// region Icons row
@Composable
private fun IconsRow(
    itemIcons: List<PickerItemUi.Icon>,
    selectedIcon: ItemIcon?,
    color: Color,
    onIconSelected: (ItemIcon) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val lastIndex = itemIcons.lastIndex
        for ((index, item) in itemIcons.withIndex()) {
            IconItem(
                icon = item.icon,
                selected = selectedIcon == item.icon,
                color = color
            ) {
                onIconSelected(item.icon)
            }

            if (index < lastIndex && itemIcons.size >= 5) {
                SpacerWeight(weight = 1f)
            } else {
                SpacerHor(width = 20.dp)
            }
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

// region Section divider
@Composable
private fun SectionDivider(
    title: String
) {
    SpacerVer(height = 20.dp)
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
    SpacerVer(height = 20.dp)
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
    items = emptyList(), // TODO: Provide preview state
    searchQuery = ""
)
// endregion