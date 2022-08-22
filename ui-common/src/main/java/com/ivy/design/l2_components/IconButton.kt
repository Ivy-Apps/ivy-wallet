package com.ivy.design.l2_components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ivy.design.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.White
import com.ivy.design.l1_buildingBlocks.IvyIcon
import com.ivy.design.l1_buildingBlocks.data.Background
import com.ivy.design.l1_buildingBlocks.data.background
import com.ivy.design.l1_buildingBlocks.data.clipBackground
import com.ivy.design.utils.ComponentPreviewBase
import com.ivy.design.utils.padding

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    iconTint: Color = White,
    background: Background = Background.Solid(
        color = UI.colors.primary,
        shape = CircleShape,
        padding = padding(all = 8.dp)
    ),
    onClick: () -> Unit
) {
    IvyIcon(
        modifier = modifier
            .clipBackground(background)
            .clickable {
                onClick()
            }
            .background(background),
        icon = icon,
        tint = iconTint
    )
}

@Preview
@Composable
private fun Preview() {
    ComponentPreviewBase {
        IconButton(
            icon = R.drawable.ic_add
        ) {

        }
    }
}