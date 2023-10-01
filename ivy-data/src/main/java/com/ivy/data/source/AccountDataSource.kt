package com.ivy.data.source

import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.write.WriteAccountDao
import javax.inject.Inject

class AccountDataSource @Inject constructor(
    private val accountDao: AccountDao,
    private val writeAccountDao: WriteAccountDao,
) {
    // TODO: Implement
}