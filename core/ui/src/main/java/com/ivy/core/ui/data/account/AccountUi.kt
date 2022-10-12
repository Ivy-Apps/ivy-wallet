package com.ivy.core.ui.data.account

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.R
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.data.icon.dummyIconSized
import com.ivy.design.l0_system.color.Green
import java.util.*

@Immutable
data class AccountUi(
    val id: String,
    val name: String,
    val color: Color,
    val icon: ItemIcon,
    val excluded: Boolean,
)

fun dummyAccountUi(
    name: String = "Account",
    id: String = UUID.randomUUID().toString(),
    color: Color = Green,
    icon: ItemIcon = dummyIconSized(R.drawable.ic_custom_account_s),
    excluded: Boolean = false,
) = AccountUi(
    id = id,
    name = name,
    color = color,
    icon = icon,
    excluded = excluded,
)