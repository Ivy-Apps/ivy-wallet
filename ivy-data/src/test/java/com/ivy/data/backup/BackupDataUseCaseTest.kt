package com.ivy.data.backup

import com.ivy.testing.TestDispatchersProvider
import com.ivy.testing.testResourceUri
import io.kotest.core.spec.style.FreeSpec
import io.mockk.mockk

class BackupDataUseCaseTest : FreeSpec({
    fun newBackupLogic(): BackupLogic = BackupLogic(
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
        json = mockk(relaxed = true),
        dispatchersProvider = TestDispatchersProvider,
    )

    fun backupZipTestCase(backupVersion: String) {
        // given
        val backupZipUri = testResourceUri("backups/$backupVersion.zip")
    }

    "imports backup from 4.5.0 (150)" {
        backupZipTestCase("450-150")
    }
})