package com.ivy.core.ui.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.R
import com.ivy.core.ui.component.BadgeComponent
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.IvyIcon
import com.ivy.design.l0_system.color.Black
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.util.ComponentPreview

@Composable
fun CategoryUi.Badge(
    background: Color = color,
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
        dummyCategoryUi(
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
        dummyCategoryUi(
            name = "Cash",
            icon = IvyIcon.Sized(
                iconS = R.drawable.ic_custom_category_s,
                iconM = 0,
                iconL = 0,
                iconId = null
            ),
            color = Purple,
        ).Badge()
    }
}
