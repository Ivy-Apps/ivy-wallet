package com.ivy.core.ui.account.folder.edit

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.core.ui.data.icon.ItemIcon

@Immutable
internal data class EditAccFolderState(
    val icon: ItemIcon,
    val color: Color,
    val initialName: String,
    val accounts: List<AccountUi>,
)