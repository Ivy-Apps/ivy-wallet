package com.ivy.data.repository.fake

import com.ivy.base.TestCoroutineScope
import com.ivy.data.DataObserver
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.db.dao.write.WriteSettingsDao
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.impl.AccountRepositoryImpl
import com.ivy.data.repository.mapper.AccountMapper
import com.ivy.base.TestDispatchersProvider
import org.jetbrains.annotations.VisibleForTesting

@VisibleForTesting
class FakeAccountRepository(
    accountDao: AccountDao,
    writeAccountDao: WriteAccountDao,
    settingsDao: SettingsDao,
    writeSettingsDao: WriteSettingsDao,
    private val accountRepository: AccountRepository = AccountRepositoryImpl(
        mapper = AccountMapper(
            FakeCurrencyRepository(
                settingsDao = settingsDao,
                writeSettingsDao = writeSettingsDao
            )
        ),
        accountDao = accountDao,
        writeAccountDao = writeAccountDao,
        dispatchersProvider = TestDispatchersProvider,
        memoFactory = fakeRepositoryMakeFactory()
    )
) : AccountRepository by accountRepository