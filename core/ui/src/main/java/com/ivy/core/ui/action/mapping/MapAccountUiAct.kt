package com.ivy.core.ui.action.mapping

import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.IconAct
import com.ivy.core.ui.data.AccountUi
import com.ivy.data.account.Account
import com.ivy.design.l0_system.color.toComposeColor
import javax.inject.Inject

class MapAccountUiAct @Inject constructor(
    private val iconAct: IconAct
) : MapUiAction<Account, AccountUi>() {
    override suspend fun transform(domain: Account): AccountUi = AccountUi(
        id = domain.id.toString(),
        name = domain.name,
        icon = iconAct(IconAct.Input(iconId = domain.icon, defaultTo = DefaultTo.Account)),
        color = domain.color.toComposeColor()
    )
}