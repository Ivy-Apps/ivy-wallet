package com.ivy.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.color.Blue2Dark
import com.ivy.design.l0_system.color.rememberContrast
import com.ivy.design.l1_buildingBlocks.Caption
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.thenIf

// TODO: Consider unifying and merging with AccountButton

@Composable
fun BadgeComponent(
    text: String,
    icon: ItemIcon,
    background: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .background(background, UI.shapes.fullyRounded)
            .thenIf(onClick != null) {
                clip(UI.shapes.fullyRounded)
                    .clickable(onClick = onClick!!)
            }
            .padding(start = 8.dp, end = 18.dp)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contrastColor = rememberContrast(background)
        ItemIcon(
            itemIcon = icon,
            size = IconSize.S,
            tint = contrastColor,
        )
        Caption(
            modifier = Modifier
                .padding(start = 4.dp)
                .widthIn(min = 0.dp, max = 120.dp),
            text = text,
            color = contrastColor,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Preview
@Composable
private fun Preview_Badge() {
    ComponentPreview {
        BadgeComponent(
            text = "Text",
            icon = ItemIcon.Unknown(
                icon = R.drawable.ic_vue_transport_car,
                iconId = null
            ),
            background = Blue2Dark
        )
    }
}