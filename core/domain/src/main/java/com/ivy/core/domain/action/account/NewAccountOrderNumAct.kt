package com.ivy.core.domain.action.account

import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.account.AccountDao
import javax.inject.Inject

class NewAccountOrderNumAct @Inject constructor(
    private val accountDao: AccountDao
) : Action<Unit, Double>() {
    override suspend fun Unit.willDo(): Double = accountDao.findMaxOrderNum() ?: 0.0
}