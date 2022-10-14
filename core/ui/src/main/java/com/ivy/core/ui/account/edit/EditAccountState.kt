package com.ivy.core.ui.account.edit

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.data.account.AccountFolderUi
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.CurrencyCode

@Immutable
internal data class EditAccountState(
    val currency: CurrencyCode,
    val icon: ItemIcon,
    val color: Color,
    val initialName: String,
    val folder: AccountFolderUi?,
    val excluded: Boolean,
    val archived: Boolean,
)