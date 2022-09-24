package com.ivy.core.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.R
import com.ivy.core.ui.component.BadgeComponent
import com.ivy.core.ui.data.AccountUi
import com.ivy.core.ui.data.dummyAccountUi
import com.ivy.core.ui.data.icon.IvyIcon
import com.ivy.design.l0_system.color.Black
import com.ivy.design.l0_system.color.Green
import com.ivy.design.util.ComponentPreview

@Composable
fun AccountUi.Badge(
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
        dummyAccountUi(
            name = "Cash",
            icon = IvyIcon.Sized(
                iconS = R.drawable.ic_custom_account_s,
                iconM = 0,
                iconL = 0,
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
        dummyAccountUi(
            name = "Cash",
            icon = IvyIcon.Sized(
                iconS = R.drawable.ic_custom_account_s,
                iconM = 0,
                iconL = 0,
                iconId = null
            ),
            color = Green,
        ).Badge()
    }
}