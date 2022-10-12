package com.ivy.core.ui.action.mapping.account

import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.action.mapping.MapUiAction
import com.ivy.core.ui.data.account.AccountFolderUi
import com.ivy.data.account.AccountFolder
import com.ivy.design.l0_system.color.toComposeColor
import javax.inject.Inject

class MapAccountFolderUiAct @Inject constructor(
    private val itemIconAct: ItemIconAct,
    private val mapAccountUiAct: MapAccountUiAct,
) : MapUiAction<AccountFolder, AccountFolderUi>() {
    override suspend fun transform(domain: AccountFolder) = AccountFolderUi(
        name = domain.name,
        icon = itemIconAct(ItemIconAct.Input(domain.icon, DefaultTo.Folder)),
        color = domain.color.toComposeColor(),
        orderNum = domain.orderNum,
        accounts = domain.accounts.map { mapAccountUiAct(it) },
    )
}