package com.ivy.wallet.domain.action.account

import com.ivy.frp.action.FPAction
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.io.persistence.dao.AccountDao
import javax.inject.Inject

class AccountsAct @Inject constructor(
    private val accountDao: AccountDao
) : FPAction<Unit, List<Account>>() {

    override suspend fun Unit.compose(): suspend () -> List<Account> = suspend {
        io { accountDao.findAll().map { it.toDomain() } }
    }
}