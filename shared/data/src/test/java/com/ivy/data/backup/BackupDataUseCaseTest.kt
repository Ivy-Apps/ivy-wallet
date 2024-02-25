package com.ivy.data.backup

import com.ivy.base.di.KotlinxSerializationModule
import com.ivy.data.db.dao.fake.FakeAccountDao
import com.ivy.data.db.dao.fake.FakeBudgetDao
import com.ivy.data.db.dao.fake.FakeCategoryDao
import com.ivy.data.db.dao.fake.FakeLoanDao
import com.ivy.data.db.dao.fake.FakeLoanRecordDao
import com.ivy.data.db.dao.fake.FakePlannedPaymentDao
import com.ivy.data.db.dao.fake.FakeSettingsDao
import com.ivy.data.db.dao.fake.FakeTransactionDao
import com.ivy.data.repository.fake.FakeAccountRepository
import com.ivy.data.repository.fake.FakeCurrencyRepository
import com.ivy.data.repository.mapper.AccountMapper
import com.ivy.testing.TestDispatchersProvider
import com.ivy.testing.testResource
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class BackupDataUseCaseTest : FreeSpec({
    fun newBackupDataUseCase(
        accountDao: FakeAccountDao = FakeAccountDao(),
        categoryDao: FakeCategoryDao = FakeCategoryDao(),
        transactionDao: FakeTransactionDao = FakeTransactionDao(),
        plannedPaymentDao: FakePlannedPaymentDao = FakePlannedPaymentDao(),
        budgetDao: FakeBudgetDao = FakeBudgetDao(),
        settingsDao: FakeSettingsDao = FakeSettingsDao(),
        loanDao: FakeLoanDao = FakeLoanDao(),
        loanRecordDao: FakeLoanRecordDao = FakeLoanRecordDao(),
    ): BackupDataUseCase = BackupDataUseCase(
        accountDao = accountDao,
        accountMapper = AccountMapper(
            FakeCurrencyRepository(
                settingsDao = settingsDao,
                writeSettingsDao = settingsDao
            )
        ),
        accountRepository = FakeAccountRepository(
            accountDao = accountDao,
            writeAccountDao = accountDao,
            settingsDao = settingsDao,
            writeSettingsDao = settingsDao
        ),
        budgetDao = budgetDao,
        categoryDao = categoryDao,
        loanRecordDao = loanRecordDao,
        loanDao = loanDao,
        plannedPaymentRuleDao = plannedPaymentDao,
        settingsDao = settingsDao,
        transactionDao = transactionDao,
        categoryWriter = categoryDao,
        transactionWriter = transactionDao,
        settingsWriter = settingsDao,
        budgetWriter = budgetDao,
        loanWriter = loanDao,
        loanRecordWriter = loanRecordDao,
        plannedPaymentRuleWriter = plannedPaymentDao,

        context = mockk(relaxed = true),
        sharedPrefs = mockk(relaxed = true),
        json = KotlinxSerializationModule.provideJson(),
        dispatchersProvider = TestDispatchersProvider,
        fileReader = mockk(relaxed = true)
    )

    suspend fun backupTestCase(backupVersion: String) {
        // given
        val originalBackupUseCase = newBackupDataUseCase()
        val backupJsonData = testResource("backups/$backupVersion.json")
            .readText(Charsets.UTF_16)

        // when
        val importedDataRes = originalBackupUseCase.importJson(backupJsonData, onProgress = {})

        // then
        importedDataRes.accountsImported shouldBeGreaterThan 0
        importedDataRes.transactionsImported shouldBeGreaterThan 0
        importedDataRes.categoriesImported shouldBeGreaterThan 0
        importedDataRes.failedRows.size shouldBe 0

        // Also - exporting and re-importing the data should work
        // given
        val exportedJson = originalBackupUseCase.generateJsonBackup()

        // when
        val freshBackupUseCase = newBackupDataUseCase()
        val reImportedDataRes = freshBackupUseCase.importJson(exportedJson, onProgress = {})

        // then
        reImportedDataRes shouldBe importedDataRes

        // Finally, exporting again should yield the same result
        freshBackupUseCase.generateJsonBackup() shouldBe exportedJson
    }

    "backup compatibility with 4.5.0 (150)" {
        backupTestCase("450-150")
    }
})