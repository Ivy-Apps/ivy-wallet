package com.ivy.core.domain.action.account.folder

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.account.AccountFolderDao
import javax.inject.Inject

class NewAccountFolderOrderNumAct @Inject constructor(
    private val accountFolderDao: AccountFolderDao
) : Action<Unit, Double>() {
    override suspend fun Unit.willDo(): Double = accountFolderDao.findMaxOrderNum() ?: 0.0
}