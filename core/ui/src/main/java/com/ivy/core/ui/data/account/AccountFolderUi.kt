package com.ivy.core.ui.data.account

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.R
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.core.ui.data.icon.dummyIconUnknown
import com.ivy.design.l0_system.color.Purple

@Immutable
data class AccountFolderUi(
    val name: String,
    val icon: ItemIcon,
    val color: Color,
    val orderNum: Double,
    val accounts: List<AccountUi>,
)

fun dummyAccountFolderUi(
    name: String = "Folder",
    icon: ItemIcon = dummyIconUnknown(R.drawable.ic_vue_files_folder),
    color: Color = Purple,
    orderNum: Double = 0.0,
    accounts: List<AccountUi> = emptyList(),
) = AccountFolderUi(
    name = name,
    icon = icon,
    color = color,
    orderNum = orderNum,
    accounts = accounts
)