package com.ivy.data.repository.fake

import com.ivy.data.db.dao.fake.FakeSettingsDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.write.WriteSettingsDao
import com.ivy.data.repository.CurrencyRepository
import com.ivy.data.repository.impl.CurrencyRepositoryImpl
import com.ivy.testing.TestDispatchersProvider
import org.jetbrains.annotations.VisibleForTesting

@VisibleForTesting
class FakeCurrencyRepository(
    settingsDao: SettingsDao = FakeSettingsDao(),
    writeSettingsDao: WriteSettingsDao = FakeSettingsDao(),
    private val currencyRepository: CurrencyRepository = CurrencyRepositoryImpl(
        settingsDao = settingsDao,
        writeSettingsDao = writeSettingsDao,
        dispatchersProvider = TestDispatchersProvider,
    )
) : CurrencyRepository by currencyRepository