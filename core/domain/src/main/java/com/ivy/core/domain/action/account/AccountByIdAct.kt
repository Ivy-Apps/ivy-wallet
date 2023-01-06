package com.ivy.core.domain.action.account

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.data.account.Account
import javax.inject.Inject

class AccountByIdAct @Inject constructor(
    private val accountDao: AccountDao
) : Action<String, Account?>() {
    override suspend fun String.willDo(): Account? =
        accountDao.findById(this)?.let(::toDomain)
}