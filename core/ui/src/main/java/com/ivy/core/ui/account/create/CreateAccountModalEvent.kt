package com.ivy.core.ui.account.create

import androidx.compose.ui.graphics.Color
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.CurrencyCode
import com.ivy.data.ItemIconId

internal sealed interface CreateAccountModalEvent {
    data class CreateAccount(
        val name: String,
        val icon: ItemIcon,
        val color: Color,
    ) : CreateAccountModalEvent

    data class IconPick(
        val iconId: ItemIconId
    ) : CreateAccountModalEvent

    data class NameChange(
        val name: String
    ) : CreateAccountModalEvent

    data class CurrencyChange(
        val newCurrency: CurrencyCode,
    ) : CreateAccountModalEvent
}