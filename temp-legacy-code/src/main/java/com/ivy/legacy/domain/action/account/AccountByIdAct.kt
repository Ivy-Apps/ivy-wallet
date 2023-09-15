package com.ivy.wallet.domain.action.account

import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.core.data.model.Account
import com.ivy.core.data.db.dao.AccountDao
import java.util.*
import javax.inject.Inject

class AccountByIdAct @Inject constructor(
    private val accountDao: AccountDao
) : FPAction<UUID, Account?>() {
    override suspend fun UUID.compose(): suspend () -> Account? = suspend {
        this // accountId
    } then accountDao::findById then {
        it?.toDomain()
    }
}
