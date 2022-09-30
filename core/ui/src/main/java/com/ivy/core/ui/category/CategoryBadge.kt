package com.ivy.core.ui.category

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.R
import com.ivy.core.ui.component.BadgeComponent
import com.ivy.core.ui.data.CategoryUi
import com.ivy.core.ui.data.dummyCategoryUi
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.core.ui.data.icon.dummyIconUnknown
import com.ivy.design.l0_system.color.Black
import com.ivy.design.l0_system.color.Purple
import com.ivy.design.util.ComponentPreview

@Composable
fun CategoryBadge(
    category: CategoryUi,
    background: Color = category.color,
    onClick: (() -> Unit)? = null
) {
    BadgeComponent(
        icon = category.icon,
        text = category.name,
        background = background,
        onClick = onClick,
    )
}

@Preview
@Composable
private fun Preview_Black() {
    ComponentPreview {
        CategoryBadge(
            category = dummyCategoryUi(
                name = "Cash",
                icon = dummyIconUnknown(R.drawable.ic_vue_building_house)
            ),
            background = Black
        )
    }
}

@Preview
@Composable
private fun Preview_Color() {
    ComponentPreview {

        CategoryBadge(
            category = dummyCategoryUi(
                name = "Cash",
                icon = dummyIconSized(R.drawable.ic_custom_category_s),
                color = Purple,
            )
        )
    }
}
