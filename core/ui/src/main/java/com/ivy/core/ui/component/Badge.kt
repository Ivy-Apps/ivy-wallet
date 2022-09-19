package com.ivy.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
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
import com.ivy.core.ui.color.contrast
import com.ivy.core.ui.icon.ItemIcon
import com.ivy.data.icon.IconSize
import com.ivy.data.icon.IvyIcon
import com.ivy.design.l0_system.Blue2Dark
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.design.l1_buildingBlocks.IvyText
import com.ivy.design.l1_buildingBlocks.SpacerHor
import com.ivy.design.util.ComponentPreview
import com.ivy.design.util.thenIf

@Composable
fun BadgeComponent(
    text: String,
    icon: IvyIcon,
    background: Color,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .background(background, UI.shapes.rFull)
            .thenIf(onClick != null) {
                clip(UI.shapes.rFull)
                    .clickable(onClick = onClick!!)
            }
            .padding(start = 8.dp, end = 18.dp)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val contrastColor = background.contrast()

        icon.ItemIcon(
            size = IconSize.S,
            tint = contrastColor,
        )

        SpacerHor(width = 4.dp)

        IvyText(
            text = text,
            typo = UI.typo.c.style(
                color = contrastColor,
                fontWeight = FontWeight.ExtraBold
            )
        )
    }
}

@Preview
@Composable
private fun Preview_Badge() {
    ComponentPreview {
        BadgeComponent(
            text = "Text",
            icon = IvyIcon.Unknown(
                icon = R.drawable.ic_vue_transport_car,
                iconId = null
            ),
            background = Blue2Dark
        )
    }
}