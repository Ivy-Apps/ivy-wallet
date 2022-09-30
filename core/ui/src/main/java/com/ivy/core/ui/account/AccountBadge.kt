package com.ivy.core.ui.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.ivy.core.ui.R
import com.ivy.core.ui.component.BadgeComponent
import com.ivy.core.ui.data.AccountUi
import com.ivy.core.ui.data.dummyAccountUi
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.design.l0_system.color.Black
import com.ivy.design.l0_system.color.Green
import com.ivy.design.util.ComponentPreview

@Composable
fun AccountBadge(
    account: AccountUi,
    background: Color = account.color,
    onClick: (() -> Unit)? = null
) {
    BadgeComponent(
        icon = account.icon,
        text = account.name,
        background = background,
        onClick = onClick,
    )
}

@Preview
@Composable
private fun Preview_Black() {
    ComponentPreview {
        AccountBadge(
            account = dummyAccountUi(
                name = "Cash",
                icon = dummyIconSized(R.drawable.ic_custom_account_s)
            ),
            background = Black
        )
    }
}

@Preview
@Composable
private fun Preview_Color() {
    ComponentPreview {
        AccountBadge(
            account = dummyAccountUi(
                name = "Cash",
                icon = dummyIconSized(R.drawable.ic_custom_account_s),
                color = Green,
            )
        )
    }
}