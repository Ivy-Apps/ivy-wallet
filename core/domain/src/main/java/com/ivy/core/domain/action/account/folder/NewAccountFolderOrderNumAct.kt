package com.ivy.core.domain.action.account.folder

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.dao.account.AccountFolderDao
import java.lang.Double.max
import javax.inject.Inject

class NewAccountFolderOrderNumAct @Inject constructor(
    private val accountFolderDao: AccountFolderDao,
    private val accountDao: AccountDao,
) : Action<Unit, Double>() {
    override suspend fun Unit.willDo(): Double = max(
        accountFolderDao.findMaxOrderNum() ?: 0.0,
        accountDao.findMaxOrderNum() ?: 0.0
    ) + 1
}