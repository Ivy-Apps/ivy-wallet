package com.ivy.core.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.domain.pure.dummy.dummyAcc
import com.ivy.core.ui.R
import com.ivy.core.ui.component.BadgeComponent
import com.ivy.data.account.Account
import com.ivy.data.icon.IvyIcon
import com.ivy.design.l0_system.Black
import com.ivy.design.l0_system.Green
import com.ivy.design.l0_system.toComposeColor
import com.ivy.design.util.ComponentPreview

@Composable
fun Account.Badge(
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
        dummyAcc(
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
        dummyAcc(
            name = "Cash",
            icon = IvyIcon.Sized(
                iconS = R.drawable.ic_custom_account_s,
                iconM = 0,
                iconL = 0,
                iconId = null
            ),
            color = Green.toArgb(),
        ).Badge()
    }
}