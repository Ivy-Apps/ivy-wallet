package com.ivy.core.ui.account.create

import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.data.account.AccountFolderUi
import com.ivy.data.CurrencyCode
import com.ivy.data.ItemIconId

internal sealed interface CreateAccountModalEvent {
    data class CreateAccount(
        val color: Color,
        val excluded: Boolean,
        val folder: AccountFolderUi?
    ) : CreateAccountModalEvent

    data class IconChange(val iconId: ItemIconId) : CreateAccountModalEvent

    data class NameChange(val name: String) : CreateAccountModalEvent

    data class CurrencyChange(val newCurrency: CurrencyCode) : CreateAccountModalEvent
}