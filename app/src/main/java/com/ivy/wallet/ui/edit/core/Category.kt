package com.ivy.wallet.ui.edit.core

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.wallet.R
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.ui.theme.Gradient
import com.ivy.wallet.ui.theme.IvyTheme
import com.ivy.wallet.ui.theme.components.IvyBorderButton
import com.ivy.wallet.ui.theme.components.IvyButton
import com.ivy.wallet.ui.theme.components.getCustomIconIdS
import com.ivy.wallet.ui.theme.findContrastTextColor
import com.ivy.wallet.ui.theme.toComposeColor

@Composable
fun Category(
    category: Category?,
    onChooseCategory: () -> Unit
) {
    if (category != null) {
        CategoryButton(category = category) {
            onChooseCategory()
        }
    } else {
        IvyBorderButton(
            modifier = Modifier.padding(start = 24.dp),
            iconStart = R.drawable.ic_plus,
            iconTint = IvyTheme.colors.pureInverse,
            text = "Add category"
        ) {
            onChooseCategory()
        }
    }
}

@Composable
private fun CategoryButton(
    category: Category,
    onClick: () -> Unit,
) {
    val contrastColor = findContrastTextColor(category.color.toComposeColor())
    IvyButton(
        modifier = Modifier.padding(start = 24.dp),
        text = category.name,
        iconStart = getCustomIconIdS(
            iconName = category.icon,
            defaultIcon = R.drawable.ic_custom_category_s
        ),
        backgroundGradient = Gradient.from(category.color, category.color),
        textStyle = UI.typo.b2.style(
            color = contrastColor,
            fontWeight = FontWeight.Bold
        ),
        iconTint = contrastColor,
        iconEnd = R.drawable.ic_onboarding_next_arrow,
        wrapContentMode = true,
        onClick = onClick
    )
}