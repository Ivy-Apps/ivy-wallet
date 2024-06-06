package com.ivy.data.repository.fake

import com.ivy.base.TestDispatchersProvider
import com.ivy.base.TimeProvider
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteAccountDao
import com.ivy.data.db.dao.write.WriteSettingsDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.model.Transaction
import com.ivy.data.repository.TransactionRepository
import com.ivy.data.repository.impl.TransactionRepositoryImpl
import com.ivy.data.repository.mapper.TransactionMapper
import org.jetbrains.annotations.VisibleForTesting

@VisibleForTesting
class FakeTransactionRepository(
    transactionDao: TransactionDao,
    writeTransactionDao: WriteTransactionDao,
    accountDao: AccountDao,
    writeAccountDao: WriteAccountDao,
    settingsDao: SettingsDao,
    writeSettingsDao: WriteSettingsDao,
    tagRepository: FakeTagRepository,
    private val transactionRepository: TransactionRepository = TransactionRepositoryImpl(
        mapper = TransactionMapper(
            accountRepository = FakeAccountRepository(
                accountDao = accountDao,
                writeAccountDao = writeAccountDao,
                settingsDao = settingsDao,
                writeSettingsDao = writeSettingsDao
            ),
            timeProvider = TimeProvider()
        ),
        transactionDao = transactionDao,
        writeTransactionDao = writeTransactionDao,
        tagRepository = tagRepository,
        dispatchersProvider = TestDispatchersProvider,
    )
) : TransactionRepository by transactionRepository {

    override suspend fun save(value: Transaction) {
        TODO("Not yet implemented")
    }
    override suspend fun saveMany(values: List<Transaction>) {
        values.forEach { save(it) }
    }
}
