package com.ivy.core.ui.account.folder.create

import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.data.ItemIconId

internal sealed interface CreateAccFolderEvent {
    data class CreateFolder(
        val color: Color,
        val accounts: List<AccountUi>,
    ) : CreateAccFolderEvent

    data class NameChange(val name: String) : CreateAccFolderEvent

    data class IconChange(val iconId: ItemIconId) : CreateAccFolderEvent
}