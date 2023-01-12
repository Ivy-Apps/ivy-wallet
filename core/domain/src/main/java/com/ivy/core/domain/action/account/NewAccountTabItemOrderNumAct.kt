package com.ivy.core.domain.action.account

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.dao.account.AccountFolderDao
import javax.inject.Inject

class NewAccountTabItemOrderNumAct @Inject constructor(
    private val accountDao: AccountDao,
    private val folderDao: AccountFolderDao,
) : Action<Unit, Double>() {
    override suspend fun action(input: Unit): Double = currentMax() + 1

    private suspend fun currentMax(): Double = maxOf(accountMax(), folderMax())

    private suspend fun accountMax(): Double = accountDao.findMaxOrderNum() ?: 0.0
    private suspend fun folderMax(): Double = folderDao.findMaxOrderNum() ?: 0.0
}