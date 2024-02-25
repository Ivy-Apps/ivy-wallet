package com.ivy.data.backup

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ivy.base.di.KotlinxSerializationModule
import com.ivy.base.legacy.SharedPrefs
import com.ivy.data.db.IvyRoomDatabase
import com.ivy.data.file.IvyFileReader
import com.ivy.data.repository.fake.FakeAccountRepository
import com.ivy.data.repository.fake.FakeCurrencyRepository
import com.ivy.data.repository.mapper.AccountMapper
import com.ivy.base.TestDispatchersProvider
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class BackupDataUseCaseAndroidTest {

    private lateinit var db: IvyRoomDatabase
    private lateinit var useCase: BackupDataUseCase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, IvyRoomDatabase::class.java).build()
        val appContext = InstrumentationRegistry.getInstrumentation().context
        useCase = BackupDataUseCase(
            accountDao = db.accountDao,
            budgetDao = db.budgetDao,
            categoryDao = db.categoryDao,
            loanRecordDao = db.loanRecordDao,
            loanDao = db.loanDao,
            plannedPaymentRuleDao = db.plannedPaymentRuleDao,
            settingsDao = db.settingsDao,
            transactionDao = db.transactionDao,
            sharedPrefs = SharedPrefs(appContext),
            accountRepository = FakeAccountRepository(
                accountDao = db.accountDao,
                writeAccountDao = db.writeAccountDao,
                settingsDao = db.settingsDao,
                writeSettingsDao = db.writeSettingsDao,
            ),
            accountMapper = AccountMapper(
                FakeCurrencyRepository(
                    settingsDao = db.settingsDao,
                    writeSettingsDao = db.writeSettingsDao,
                )
            ),
            categoryWriter = db.writeCategoryDao,
            transactionWriter = db.writeTransactionDao,
            settingsWriter = db.writeSettingsDao,
            budgetWriter = db.writeBudgetDao,
            loanWriter = db.writeLoanDao,
            loanRecordWriter = db.writeLoanRecordDao,
            plannedPaymentRuleWriter = db.writePlannedPaymentRuleDao,
            context = appContext,
            json = KotlinxSerializationModule.provideJson(),
            dispatchersProvider = TestDispatchersProvider,
            fileReader = IvyFileReader(appContext)
        )
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun backup450_150() = runBlocking {
        backupTestCase("450-150")
    }

    private suspend fun backupTestCase(version: String) {
        importBackupZipTestCase(version)
        importBackupJsonTestCase(version)

        // close and re-open the db to ensure fresh data
        closeDb()
        createDb()
        exportsAndImportsTestCase(version)
    }

    private suspend fun importBackupZipTestCase(version: String) {
        // given
        val backupUri = copyTestResourceToInternalStorage("backups/$version.zip")

        // when
        val res = useCase.importBackupFile(backupUri, onProgress = {})

        // then
        res.shouldBeSuccessful()
    }

    private suspend fun importBackupJsonTestCase(version: String) {
        // given
        val backupUri = copyTestResourceToInternalStorage("backups/$version.json")

        // when
        val res = useCase.importBackupFile(backupUri, onProgress = {})

        // then
        res.shouldBeSuccessful()
    }

    private suspend fun exportsAndImportsTestCase(version: String) {
        // given
        val backupUri = copyTestResourceToInternalStorage("backups/$version.zip")
        // preload data
        useCase.importBackupFile(backupUri, onProgress = {}).shouldBeSuccessful()
        val exportedFileUri = tempAndroidFile("exported", ".zip").toUri()

        // then
        useCase.exportToFile(exportedFileUri)
        val reImportRes = useCase.importBackupFile(backupUri, onProgress = {})

        // then
        reImportRes.shouldBeSuccessful()
    }

    private fun copyTestResourceToInternalStorage(resPath: String): Uri {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val assetManager = context.assets
        val inputStream = assetManager.open(resPath)
        val outputFile = tempAndroidFile("temp-backup", resPath.split(".").last())
        outputFile.outputStream().use { fileOut ->
            fileOut.write(inputStream.readBytes())
        }
        return Uri.fromFile(outputFile)
    }

    private fun tempAndroidFile(prefix: String, suffix: String): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return File.createTempFile(prefix, suffix, context.filesDir)
    }

    private fun ImportResult.shouldBeSuccessful() {
        failedRows.shouldBeEmpty()
        categoriesImported shouldBeGreaterThan 0
        accountsImported shouldBeGreaterThan 0
        transactionsImported shouldBeGreaterThan 0
    }
}