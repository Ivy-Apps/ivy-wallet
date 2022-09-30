package com.ivy.design.l1_buildingBlocks

import androidx.annotation.DrawableRes
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.design.R
import com.ivy.design.l0_system.UI
import com.ivy.design.util.ComponentPreview

@Composable
fun IconRes(
    @DrawableRes
    icon: Int,
    modifier: Modifier = Modifier,
    tint: Color = UI.colorsInverted.pure,
    contentDescription: String = "icon"
) {
    Icon(
        modifier = modifier,
        painter = painterResource(id = icon),
        contentDescription = contentDescription,
        tint = tint
    )
}


// region Preview
@Preview
@Composable
private fun Preview() {
    ComponentPreview {
        IconRes(
            icon = R.drawable.ic_ivy_logo,
            tint = Color.Unspecified,
        )
    }
}
// endregion