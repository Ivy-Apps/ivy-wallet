package com.ivy.wallet.domain.action.account

import com.google.firebase.crashlytics.internal.model.ImmutableList
import com.ivy.frp.action.FPAction
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.utils.toActualImmutableList
import javax.inject.Inject

class AccountsAct @Inject constructor(
    private val accountDao: AccountDao
) : FPAction<Unit, ImmutableList<Account>>() {

    override suspend fun Unit.compose(): suspend () -> ImmutableList<Account> = suspend {
        io { accountDao.findAll().map { it.toDomain() }.toActualImmutableList() }
    }
}
