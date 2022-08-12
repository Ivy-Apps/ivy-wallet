package com.ivy.wallet.domain.action.account

import com.ivy.data.AccountOld
import com.ivy.frp.action.FPAction
import com.ivy.wallet.io.persistence.dao.AccountDao
import javax.inject.Inject

@Deprecated("Use AccountsAct from `:core:actions`")
class AccountsActOld @Inject constructor(
    private val accountDao: AccountDao
) : FPAction<Unit, List<AccountOld>>() {

    override suspend fun Unit.compose(): suspend () -> List<AccountOld> = suspend {
        io { accountDao.findAll().map { it.toDomain() } }
    }
}