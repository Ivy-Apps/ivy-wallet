package com.ivy.core.ui.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.functions.category.dummyCategory
import com.ivy.core.ui.R
import com.ivy.core.ui.component.BadgeComponent
import com.ivy.core.ui.temp.ComponentPreview
import com.ivy.data.category.Category
import com.ivy.data.icon.IvyIcon
import com.ivy.design.l0_system.Black
import com.ivy.design.l0_system.Purple
import com.ivy.design.l0_system.toComposeColor

@Composable
fun Category.Badge(
    background: Color = color.toComposeColor(),
    onClick: (() -> Unit)? = null
) {
    BadgeComponent(
        icon = icon,
        text = name,
        background = background,
        onClick = onClick,
    )
}

@Preview
@Composable
private fun Preview_Black() {
    ComponentPreview {
        dummyCategory(
            name = "Cash",
            icon = IvyIcon.Unknown(
                icon = R.drawable.ic_vue_building_house,
                iconId = null
            )
        ).Badge(
            background = Black
        )
    }
}

@Preview
@Composable
private fun Preview_Color() {
    ComponentPreview {
        dummyCategory(
            name = "Cash",
            icon = IvyIcon.Sized(
                iconS = R.drawable.ic_custom_category_s,
                iconM = 0,
                iconL = 0,
                iconId = null
            ),
            color = Purple.toArgb(),
        ).Badge()
    }
}
