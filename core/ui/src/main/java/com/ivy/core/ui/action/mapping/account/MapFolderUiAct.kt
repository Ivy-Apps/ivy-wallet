package com.ivy.core.ui.action.mapping.account

import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.ItemIconAct
import com.ivy.core.ui.action.mapping.MapUiAction
import com.ivy.core.ui.data.account.FolderUi
import com.ivy.data.account.AccountFolder
import com.ivy.design.l0_system.color.toComposeColor
import javax.inject.Inject

class MapFolderUiAct @Inject constructor(
    private val itemIconAct: ItemIconAct,
) : MapUiAction<AccountFolder, FolderUi>() {
    override suspend fun transform(domain: AccountFolder) = FolderUi(
        id = domain.id,
        name = domain.name,
        icon = itemIconAct(ItemIconAct.Input(domain.icon, DefaultTo.Folder)),
        color = domain.color.toComposeColor(),
        orderNum = domain.orderNum,
    )
}