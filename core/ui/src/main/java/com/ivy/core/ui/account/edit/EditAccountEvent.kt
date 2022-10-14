package com.ivy.core.ui.account.edit

import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.data.CurrencyCode
import com.ivy.data.ItemIconId

internal sealed interface EditAccountEvent {
    data class Initial(val accountId: String) : EditAccountEvent

    object EditAccount : EditAccountEvent

    data class IconChange(val iconId: ItemIconId) : EditAccountEvent

    data class NameChange(val name: String) : EditAccountEvent

    data class CurrencyChange(val newCurrency: CurrencyCode) : EditAccountEvent

    data class ColorChange(val color: Color) : EditAccountEvent

    data class FolderChange(val folder: FolderUi?) : EditAccountEvent

    data class ExcludedChange(val excluded: Boolean) : EditAccountEvent

    object Archive : EditAccountEvent
    object Unarchive : EditAccountEvent
    object Delete : EditAccountEvent
}