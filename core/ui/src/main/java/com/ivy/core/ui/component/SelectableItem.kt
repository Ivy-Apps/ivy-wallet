package com.ivy.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.data.icon.dummyIconUnknown
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l1_buildingBlocks.B2
import com.ivy.design.l1_buildingBlocks.Caption
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.l3_ivyComponents.Visibility
import com.ivy.design.l3_ivyComponents.button.ButtonSize
import com.ivy.design.l3_ivyComponents.button.IvyButton
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.thenIf
import com.ivy.design.util.thenWhen

@Composable
fun SelectableItem(
    name: String,
    icon: ItemIcon,
    color: Color,
    selected: Boolean,
    deselectButton: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit,
    onDeselect: () -> Unit,
) {
    val dynamicContrast = rememberDynamicContrast(color)
    Row(
        modifier = modifier
            .clip(UI.shapes.fullyRounded)
            .thenWhen {
                when (selected) {
                    true -> background(color, UI.shapes.fullyRounded)
                        .border(2.dp, dynamicContrast, UI.shapes.fullyRounded)
                    false -> border(1.dp, color, UI.shapes.fullyRounded)
                }
            }
            .clickable(onClick = onSelect)
            .thenIf(selected && !deselectButton) {
                padding(vertical = 8.dp)
                    .padding(end = 24.dp)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (selected) {
            true -> SelectedContent(
                name = name,
                icon = icon,
                color = color,
                deselectButton = deselectButton,
                onDeselect = onDeselect
            )
            false -> Content(
                name = name,
                icon = icon,
                color = color
            )
        }
    }
}

@Suppress("unused")
@Composable
private fun RowScope.SelectedContent(
    name: String,
    icon: ItemIcon,
    color: Color,
    deselectButton: Boolean,
    onDeselect: () -> Unit
) {
    SpacerHor(width = 12.dp)
    val contrastColor = rememberContrast(color)
    ItemIcon(
        itemIcon = icon,
        size = IconSize.S,
        tint = contrastColor,
    )
    SpacerHor(width = 8.dp)
    B2(
        text = name,
        color = contrastColor,
        fontWeight = FontWeight.ExtraBold
    )
    if (deselectButton) {
        SpacerHor(width = 12.dp)
        IvyButton(
            modifier = Modifier.padding(all = 4.dp),
            size = ButtonSize.Small,
            visibility = Visibility.Medium,
            feeling = Feeling.Negative,
            text = null,
            icon = R.drawable.round_remove_24,
            onClick = onDeselect
        )
    }
}

@Suppress("unused")
@Composable
private fun RowScope.Content(
    name: String,
    color: Color,
    icon: ItemIcon,
) {
    ItemIcon(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .padding(start = 8.dp),
        itemIcon = icon,
        size = IconSize.S,
        tint = UI.colorsInverted.pure,
    )
    Caption(
        modifier = Modifier.padding(
            start = 8.dp, end = 16.dp
        ),
        text = name,
        color = UI.colorsInverted.pure,
        fontWeight = FontWeight.ExtraBold
    )
}


// region Preview
@Preview
@Composable
private fun Preview_Selected() {
    ComponentPreview {
        SelectableItem(
            name = "Account",
            icon = dummyIconUnknown(R.drawable.ic_vue_building_bank),
            color = Purple,
            selected = true,
            deselectButton = true,
            onSelect = {},
            onDeselect = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Selected_noDeselectButton() {
    ComponentPreview {
        SelectableItem(
            name = "Account",
            icon = dummyIconUnknown(R.drawable.ic_vue_building_bank),
            color = Purple,
            selected = true,
            deselectButton = false,
            onSelect = {},
            onDeselect = {}
        )
    }
}

@Preview
@Composable
private fun Preview_Deselected() {
    ComponentPreview {
        SelectableItem(
            name = "Account",
            icon = dummyIconUnknown(R.drawable.ic_vue_building_bank),
            color = Purple,
            selected = false,
            deselectButton = true,
            onSelect = {},
            onDeselect = {}
        )
    }
}
// endregion