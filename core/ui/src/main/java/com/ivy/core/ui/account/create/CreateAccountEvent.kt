package com.ivy.core.ui.account.create

import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.data.CurrencyCode
import com.ivy.data.ItemIconId

internal sealed interface CreateAccountEvent {
    data class CreateAccount(
        val color: Color,
        val excluded: Boolean,
        val folder: FolderUi?
    ) : CreateAccountEvent

    data class IconChange(val iconId: ItemIconId) : CreateAccountEvent

    data class NameChange(val name: String) : CreateAccountEvent

    data class CurrencyChange(val newCurrency: CurrencyCode) : CreateAccountEvent
}