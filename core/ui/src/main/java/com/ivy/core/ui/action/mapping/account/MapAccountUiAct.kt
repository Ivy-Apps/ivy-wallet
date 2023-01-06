package com.ivy.core.ui.action.mapping.account

import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.action.mapping.MapUiAction
import com.ivy.core.ui.data.account.AccountUi
import com.ivy.data.account.Account
import com.ivy.design.l0_system.color.toComposeColor
import javax.inject.Inject

class MapAccountUiAct @Inject constructor(
    private val itemIconAct: ItemIconAct
) : MapUiAction<Account, AccountUi>() {
    override suspend fun transform(domain: Account): AccountUi = AccountUi(
        id = domain.id.toString(),
        name = domain.name,
        icon = itemIconAct(ItemIconAct.Input(iconId = domain.icon, defaultTo = DefaultTo.Account)),
        color = domain.color.toComposeColor(),
        excluded = domain.excluded,
    )
}