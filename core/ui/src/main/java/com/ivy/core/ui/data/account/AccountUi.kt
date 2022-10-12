package com.ivy.core.ui.data.account

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.R
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.design.l0_system.color.Green

@Immutable
data class AccountUi(
    val id: String,
    val name: String,
    val color: Color,
    val icon: ItemIcon,
)

fun dummyAccountUi(
    name: String = "Account",
    color: Color = Green,
    icon: ItemIcon = dummyIconSized(R.drawable.ic_custom_account_s)
) = AccountUi(
    id = "",
    name = name,
    color = color,
    icon = icon
)