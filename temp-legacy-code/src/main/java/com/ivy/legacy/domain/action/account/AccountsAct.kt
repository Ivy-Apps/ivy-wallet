package com.ivy.wallet.domain.action.account

import com.ivy.core.db.read.AccountDao
import com.ivy.core.datamodel.Account
import com.ivy.frp.action.FPAction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class AccountsAct @Inject constructor(
    private val accountDao: AccountDao
) : FPAction<Unit, ImmutableList<Account>>() {

    override suspend fun Unit.compose(): suspend () -> ImmutableList<Account> = suspend {
        io { accountDao.findAll().map { it.toDomain() }.toImmutableList() }
    }
}
