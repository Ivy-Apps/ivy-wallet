package com.ivy.wallet.domain.action.account

import com.ivy.core.db.read.AccountDao
import com.ivy.core.datamodel.Account
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import java.util.UUID
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
