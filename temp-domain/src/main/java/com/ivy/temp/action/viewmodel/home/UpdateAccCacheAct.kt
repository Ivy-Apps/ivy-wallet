package com.ivy.wallet.domain.action.viewmodel.home

import com.ivy.core.ui.temp.trash.IvyWalletCtx
import com.ivy.data.AccountOld
import com.ivy.frp.action.FPAction
import javax.inject.Inject

class UpdateAccCacheAct @Inject constructor(
    private val ivyWalletCtx: IvyWalletCtx
) : FPAction<List<AccountOld>, List<AccountOld>>() {
    override suspend fun List<AccountOld>.compose(): suspend () -> List<AccountOld> = suspend {
        val accounts = this

        ivyWalletCtx.accountMap.clear()
        ivyWalletCtx.accountMap.putAll(accounts.map { it.id to it })

        accounts
    }
}