package com.ivy.core.domain.algorithm.accountcache

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.IvyWalletCoreDb
import javax.inject.Inject

class NukeAccountCacheAct @Inject constructor(
    private val db: IvyWalletCoreDb
) : Action<Unit, Unit>() {
    override suspend fun action(input: Unit) {
        db.accountCacheDao().deleteAll()
    }
}