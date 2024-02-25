package com.ivy.data.repository.fake

import com.ivy.data.DataWriteEventBus
import com.ivy.data.db.dao.fake.FakeAccountDao
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.impl.AccountRepositoryImpl
import com.ivy.data.repository.mapper.AccountMapper
import com.ivy.testing.TestDispatchersProvider
import org.jetbrains.annotations.VisibleForTesting

@VisibleForTesting
class FakeAccountRepository(
    accountDao: AccountDao = FakeAccountDao(),
    writeAccountDao: WriteAccountDao = FakeAccountDao(),
    private val accountRepository: AccountRepository = AccountRepositoryImpl(
        mapper = AccountMapper(),
        accountDao = accountDao,
        writeAccountDao = writeAccountDao,
        dispatchersProvider = TestDispatchersProvider,
        writeEventBus = DataWriteEventBus(),
    )
) : AccountRepository by accountRepository