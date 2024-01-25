package com.ivy.data.backup

import com.ivy.base.di.KotlinxSerializationModule
import com.ivy.data.backup.fake.FakeBackupAccountDao
import com.ivy.data.backup.fake.FakeBackupBudgetDao
import com.ivy.data.backup.fake.FakeBackupCategoryDao
import com.ivy.data.backup.fake.FakeBackupLoanDao
import com.ivy.data.backup.fake.FakeBackupLoanRecordDao
import com.ivy.data.backup.fake.FakeBackupPlannedPaymentDao
import com.ivy.data.backup.fake.FakeBackupSettingsDao
import com.ivy.data.backup.fake.FakeBackupTransactionDao
import com.ivy.testing.TestDispatchersProvider
import com.ivy.testing.testResource
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class BackupDataUseCaseTest : FreeSpec({
    fun newBackupDataUseCase(
        backupAccountDao: FakeBackupAccountDao = FakeBackupAccountDao(),
        backupCategoryDao: FakeBackupCategoryDao = FakeBackupCategoryDao(),
        backupTransactionDao: FakeBackupTransactionDao = FakeBackupTransactionDao(),
        backupPlannedPaymentDao: FakeBackupPlannedPaymentDao = FakeBackupPlannedPaymentDao(),
        backupBudgetDao: FakeBackupBudgetDao = FakeBackupBudgetDao(),
        backupSettingsDao: FakeBackupSettingsDao = FakeBackupSettingsDao(),
        backupLoanDao: FakeBackupLoanDao = FakeBackupLoanDao(),
        backupLoanRecordDao: FakeBackupLoanRecordDao = FakeBackupLoanRecordDao(),
    ): BackupDataUseCase = BackupDataUseCase(
        accountDao = backupAccountDao,
        accountWriter = backupAccountDao,
        budgetDao = backupBudgetDao,
        categoryDao = backupCategoryDao,
        loanRecordDao = backupLoanRecordDao,
        loanDao = backupLoanDao,
        plannedPaymentRuleDao = backupPlannedPaymentDao,
        settingsDao = backupSettingsDao,
        transactionDao = backupTransactionDao,
        categoryWriter = backupCategoryDao,
        transactionWriter = backupTransactionDao,
        settingsWriter = backupSettingsDao,
        budgetWriter = backupBudgetDao,
        loanWriter = backupLoanDao,
        loanRecordWriter = backupLoanRecordDao,
        plannedPaymentRuleWriter = backupPlannedPaymentDao,

        context = mockk(relaxed = true),
        sharedPrefs = mockk(relaxed = true),
        json = KotlinxSerializationModule.provideJson(),
        dispatchersProvider = TestDispatchersProvider,
    )

    suspend fun backupTestCase(backupVersion: String) {
        // given
        val backupDataUseCase = newBackupDataUseCase()
        val backupJson = testResource("backups/$backupVersion.json")
            .readText(Charsets.UTF_16)

        // when
        val importRes = backupDataUseCase.importJson(backupJson, onProgress = {})

        // then
        importRes.accountsImported shouldBeGreaterThan 0
        importRes.transactionsImported shouldBeGreaterThan 0
        importRes.categoriesImported shouldBeGreaterThan 0
        importRes.failedRows.size shouldBe 0

        // also
        val exportedJson = backupDataUseCase.generateJsonBackup()
        val exportImportRes2 = backupDataUseCase.importJson(exportedJson, onProgress = {})
        exportImportRes2 shouldBe importRes
    }

    "backups compatibility with 4.5.0 (150)" {
        backupTestCase("450-150")
    }

    "exports and imports a backup" {
        // given
        val backupDataUseCase = newBackupDataUseCase()

        // when
        backupDataUseCase.generateJsonBackup()
    }
})