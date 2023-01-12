package com.ivy.core.domain.action.account

import com.ivy.common.time.provider.TimeProvider
import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.data.account.Account
import javax.inject.Inject

class AccountByIdAct @Inject constructor(
    private val accountDao: AccountDao,
    private val timeProvider: TimeProvider
) : Action<String, Account?>() {
    override suspend fun action(accountId: String): Account? =
        accountDao.findById(accountId)?.let {
            toDomain(acc = it, timeProvider = timeProvider)
        }
}