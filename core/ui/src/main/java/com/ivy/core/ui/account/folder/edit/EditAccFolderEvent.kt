package com.ivy.core.ui.account.folder.edit

import androidx.compose.ui.graphics.Color
import com.ivy.data.ItemIconId

internal sealed interface EditAccFolderEvent {
    data class Initial(val folderId: String) : EditAccFolderEvent

    object EditFolder : EditAccFolderEvent

    data class NameChange(val name: String) : EditAccFolderEvent

    data class IconChange(val iconId: ItemIconId) : EditAccFolderEvent

    data class ColorChange(val color: Color) : EditAccFolderEvent

    object Delete : EditAccFolderEvent
}