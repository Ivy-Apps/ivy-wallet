package com.ivy.data.backup

import com.ivy.base.di.KotlinxSerializationModule
import com.ivy.testing.TestDispatchersProvider
import com.ivy.testing.testResource
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.mockk

class BackupDataUseCaseTest : FreeSpec({
    fun newBackupDataUseCase(): BackupDataUseCase = BackupDataUseCase(
        accountDao = mockk(relaxed = true),
        budgetDao = mockk(relaxed = true),
        categoryDao = mockk(relaxed = true),
        loanRecordDao = mockk(relaxed = true),
        loanDao = mockk(relaxed = true),
        plannedPaymentRuleDao = mockk(relaxed = true),
        settingsDao = mockk(relaxed = true),
        transactionDao = mockk(relaxed = true),
        sharedPrefs = mockk(relaxed = true),
        accountWriter = mockk(relaxed = true),
        categoryWriter = mockk(relaxed = true),
        transactionWriter = mockk(relaxed = true),
        settingsWriter = mockk(relaxed = true),
        budgetWriter = mockk(relaxed = true),
        loanWriter = mockk(relaxed = true),
        loanRecordWriter = mockk(relaxed = true),
        plannedPaymentRuleWriter = mockk(relaxed = true),
        context = mockk(relaxed = true),
        json = KotlinxSerializationModule.provideJson(),
        dispatchersProvider = TestDispatchersProvider,
    )

    suspend fun backupTestCase(backupVersion: String) {
        // given
        val backupDataUseCase = newBackupDataUseCase()
        val backupJson = testResource("backups/$backupVersion.json")
            .readText(Charsets.UTF_16)

        // when
        val result = backupDataUseCase.importJson(backupJson, onProgress = {})

        // then
        result.accountsImported shouldBeGreaterThan 0
        result.transactionsImported shouldBeGreaterThan 0
        result.categoriesImported shouldBeGreaterThan 0
        result.failedRows.size shouldBe 0
    }

    "imports backup from 4.5.0 (150)" {
        backupTestCase("450-150")
    }

    "exports and imports a backup" {
        // given
        val backupDataUseCase = newBackupDataUseCase()

        // when
        backupDataUseCase.generateJsonBackup()
    }
})