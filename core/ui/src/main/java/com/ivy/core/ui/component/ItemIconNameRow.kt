package com.ivy.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.core.ui.R
import com.ivy.core.ui.data.icon.IconSize
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.rememberDynamicContrast
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.l3_ivyComponents.Feeling
import com.ivy.design.util.ComponentPreview

@Composable
fun ItemIconNameRow(
    icon: ItemIcon,
    color: Color,
    initialName: String,
    nameInputHint: String,
    autoFocusInput: Boolean,
    modifier: Modifier = Modifier,
    onPickIcon: () -> Unit,
    onNameChange: (String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ItemIcon(
            modifier = Modifier
                .clip(UI.shapes.circle)
                .background(color, UI.shapes.circle)
                .clickable(onClick = onPickIcon)
                .padding(all = 4.dp),
            itemIcon = icon,
            size = IconSize.M,
            tint = rememberDynamicContrast(color)
        )
        SpacerHor(width = 8.dp)
        ItemNameInput(
            modifier = Modifier.weight(1f),
            initialName = initialName,
            hint = nameInputHint,
            feeling = Feeling.Custom(color),
            autoFocus = autoFocusInput,
            onNameChange = onNameChange
        )
    }
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        ItemIconNameRow(
            icon = dummyIconSized(R.drawable.ic_custom_account_m),
            color = UI.colors.primary,
            initialName = "",
            nameInputHint = "New account",
            autoFocusInput = false,
            onPickIcon = {},
            onNameChange = {},
        )
    }
}
// endregion