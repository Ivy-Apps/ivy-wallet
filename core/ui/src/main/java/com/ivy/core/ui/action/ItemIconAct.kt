package com.ivy.core.ui.action

import com.ivy.base.R
import com.ivy.core.domain.action.Action
import com.ivy.core.ui.data.icon.ItemIcon
import com.ivy.data.ItemIconId
import javax.inject.Inject

class ItemIconAct @Inject constructor(
    private val itemIconOptionalAct: ItemIconOptionalAct,
) : Action<ItemIconAct.Input, ItemIcon>() {
    data class Input(
        val iconId: ItemIconId?,
        val defaultTo: DefaultTo
    )

    override suspend fun Input.willDo(): ItemIcon {
        fun Input.default(): ItemIcon = ItemIcon.Sized(
            iconS = when (defaultTo) {
                DefaultTo.Account -> R.drawable.ic_custom_account_s
                DefaultTo.Category -> R.drawable.ic_custom_category_s
            },
            iconM = when (defaultTo) {
                DefaultTo.Account -> R.drawable.ic_custom_account_m
                DefaultTo.Category -> R.drawable.ic_custom_category_m
            },
            iconL = when (defaultTo) {
                DefaultTo.Account -> R.drawable.ic_custom_account_l
                DefaultTo.Category -> R.drawable.ic_custom_category_l
            },
            iconId = iconId
        )

        return iconId?.let { itemIconOptionalAct(it) } ?: default()
    }
}

enum class DefaultTo {
    Account,
    Category
}