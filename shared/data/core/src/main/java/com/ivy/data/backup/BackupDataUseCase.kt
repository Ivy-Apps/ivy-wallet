package com.ivy.data.backup

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.ivy.base.legacy.SharedPrefs
import com.ivy.base.legacy.unzip
import com.ivy.base.legacy.zip
import com.ivy.base.threading.DispatchersProvider
import com.ivy.data.DataObserver
import com.ivy.data.DataWriteEvent
import com.ivy.data.db.dao.read.AccountDao
import com.ivy.data.db.dao.read.BudgetDao
import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.read.LoanDao
import com.ivy.data.db.dao.read.LoanRecordDao
import com.ivy.data.db.dao.read.PlannedPaymentRuleDao
import com.ivy.data.db.dao.read.SettingsDao
import com.ivy.data.db.dao.read.TagAssociationDao
import com.ivy.data.db.dao.read.TagDao
import com.ivy.data.db.dao.read.TransactionDao
import com.ivy.data.db.dao.write.WriteBudgetDao
import com.ivy.data.db.dao.write.WriteCategoryDao
import com.ivy.data.db.dao.write.WriteLoanDao
import com.ivy.data.db.dao.write.WriteLoanRecordDao
import com.ivy.data.db.dao.write.WritePlannedPaymentRuleDao
import com.ivy.data.db.dao.write.WriteSettingsDao
import com.ivy.data.db.dao.write.WriteTagAssociationDao
import com.ivy.data.db.dao.write.WriteTagDao
import com.ivy.data.db.dao.write.WriteTransactionDao
import com.ivy.data.file.FileSystem
import com.ivy.data.repository.AccountRepository
import com.ivy.data.repository.mapper.AccountMapper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject

// TODO: Legacy code, needs improvements
class BackupDataUseCase @Inject constructor(
    private val accountDao: AccountDao,
    private val budgetDao: BudgetDao,
    private val categoryDao: CategoryDao,
    private val loanRecordDao: LoanRecordDao,
    private val loanDao: LoanDao,
    private val plannedPaymentRuleDao: PlannedPaymentRuleDao,
    private val settingsDao: SettingsDao,
    private val transactionDao: TransactionDao,
    private val transactionWriter: WriteTransactionDao,
    private val sharedPrefs: SharedPrefs,
    private val accountRepository: AccountRepository,
    private val accountMapper: AccountMapper,
    private val categoryWriter: WriteCategoryDao,
    private val settingsWriter: WriteSettingsDao,
    private val budgetWriter: WriteBudgetDao,
    private val loanWriter: WriteLoanDao,
    private val loanRecordWriter: WriteLoanRecordDao,
    private val plannedPaymentRuleWriter: WritePlannedPaymentRuleDao,
    @ApplicationContext
    private val context: Context,
    private val json: Json,
    private val dispatchersProvider: DispatchersProvider,
    private val fileSystem: FileSystem,
    private val dataObserver: DataObserver,
    private val tagsReader: TagDao,
    private val tagAssociationReader: TagAssociationDao,
    private val tagsWriter: WriteTagDao,
    private val tagAssociationWriter: WriteTagAssociationDao
) {
    suspend fun exportToFile(
        zipFileUri: Uri
    ) {
        val jsonString = generateJsonBackup()
        val file = createJsonDataFile(jsonString)
        zip(context = context, zipFileUri, listOf(file))
        clearCacheDir()
    }

    private fun createJsonDataFile(jsonString: String): File {
        val fileNamePrefix = "data"
        val fileNameSuffix = ".json"
        val outputDir = context.cacheDir

        val file = File.createTempFile(fileNamePrefix, fileNameSuffix, outputDir)
        FileOutputStream(file).use { it.write(jsonString.toByteArray(Charsets.UTF_16)) }

        return file
    }

    suspend fun generateJsonBackup(): String {
        return withContext(dispatchersProvider.io) {
            val accounts = async { accountDao.findAll() }
            val budgets = async { budgetDao.findAll() }
            val categories = async { categoryDao.findAll() }
            val loanRecords = async { loanRecordDao.findAll() }
            val loans = async { loanDao.findAll() }
            val plannedPaymentRules =
                async { plannedPaymentRuleDao.findAll() }
            val settings = async { settingsDao.findAll() }
            val transactions = async { transactionDao.findAll() }
            val sharedPrefs = async { getSharedPrefsData() }
            val tags = async { tagsReader.findAll() }
            val tagAssociations = async { tagAssociationReader.findAll() }

            val completeData = IvyWalletCompleteData(
                accounts = accounts.await(),
                budgets = budgets.await(),
                categories = categories.await(),
                loanRecords = loanRecords.await(),
                loans = loans.await(),
                plannedPaymentRules = plannedPaymentRules.await(),
                settings = settings.await(),
                transactions = transactions.await(),
                sharedPrefs = sharedPrefs.await(),
                tags = tags.await(),
                tagAssociations = tagAssociations.await()
            )

            json.encodeToString(completeData)
        }
    }

    private fun getSharedPrefsData(): HashMap<String, String> {
        val hashmap = HashMap<String, String>()
        hashmap[SharedPrefs.SHOW_NOTIFICATIONS] =
            sharedPrefs.getBoolean(SharedPrefs.SHOW_NOTIFICATIONS, true).toString()

        hashmap[SharedPrefs.APP_LOCK_ENABLED] =
            sharedPrefs.getBoolean(SharedPrefs.APP_LOCK_ENABLED, false).toString()

        hashmap[SharedPrefs.HIDE_CURRENT_BALANCE] =
            sharedPrefs.getBoolean(SharedPrefs.HIDE_CURRENT_BALANCE, false).toString()

        hashmap[SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE] =
            sharedPrefs.getBoolean(SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE, false).toString()

        return hashmap
    }

    suspend fun importBackupFile(
        backupFileUri: Uri,
        onProgress: suspend (progressPercent: Double) -> Unit
    ): ImportResult = withContext(dispatchersProvider.io) {
        return@withContext try {
            val jsonString = try {
                extractAndReadBackupZip(backupFileUri, onProgress)
            } catch (e: Exception) {
                fileSystem.read(backupFileUri, Charsets.UTF_16).getOrNull()
            } ?: ""

            importJson(jsonString, onProgress, clearCacheDir = true)
        } catch (e: Exception) {
            Timber.e("Import error: $e")
            ImportResult(
                rowsFound = 0,
                transactionsImported = 0,
                accountsImported = 0,
                categoriesImported = 0,
                failedRows = persistentListOf()
            )
        } finally {
            dataObserver.post(DataWriteEvent.AllDataChange)
        }
    }

    private suspend fun extractAndReadBackupZip(
        backupFileUri: Uri,
        onProgress: suspend (progressPercent: Double) -> Unit
    ): String? {
        val folderName = "backup" + System.currentTimeMillis()
        val cacheFolderPath = File(context.cacheDir, folderName)

        unzip(context, backupFileUri, cacheFolderPath)

        val filesArray = cacheFolderPath.listFiles()

        onProgress(0.05)

        if (filesArray == null || filesArray.isEmpty()) {
            error("Couldn't unzip")
        }

        val filesList = filesArray.toList().filter {
            hasJsonExtension(it)
        }

        onProgress(0.1)

        if (filesList.size != 1) {
            error("Didn't unzip exactly one file.")
        }

        return fileSystem.read(filesList[0].toUri(), Charsets.UTF_16).getOrNull()
    }

    suspend fun importJson(
        jsonString: String,
        onProgress: suspend (Double) -> Unit = {},
        clearCacheDir: Boolean = false,
    ): ImportResult {
        val modifiedJsonString = accommodateExistingAccountsAndCategories(jsonString)
        val ivyWalletCompleteData = modifiedJsonString?.let {
            json.decodeFromString<IvyWalletCompleteData>(it)
        } ?: error("Failed to parse backup JSON.")

        onProgress(0.4)
        insertDataToDb(completeData = ivyWalletCompleteData, onProgress = onProgress)
        onProgress(1.0)

        if (clearCacheDir) {
            clearCacheDir()
        }

        return ImportResult(
            rowsFound = ivyWalletCompleteData.transactions.size,
            transactionsImported = ivyWalletCompleteData.transactions.size,
            accountsImported = ivyWalletCompleteData.accounts.size,
            categoriesImported = ivyWalletCompleteData.categories.size,
            failedRows = persistentListOf()
        )
    }

    private suspend fun accommodateExistingAccountsAndCategories(jsonString: String?): String? {
        if (jsonString == null) return null

        val ivyWalletCompleteData = json.decodeFromString<IvyWalletCompleteData>(jsonString)
        val replacementPairs = getReplacementPairs(ivyWalletCompleteData)

        var modifiedString = jsonString
        replacementPairs.forEach {
            modifiedString = modifiedString!!.replace(it.first.toString(), it.second.toString())
        }

        return modifiedString
    }

    private suspend fun insertDataToDb(
        completeData: IvyWalletCompleteData,
        onProgress: suspend (progressPercent: Double) -> Unit = {}
    ) {
        withContext(dispatchersProvider.io) {
            transactionWriter.saveMany(completeData.transactions)
            onProgress(0.6)

            val accounts = async {
                val domainAccounts = with(accountMapper) {
                    completeData.accounts.mapNotNull { entity ->
                        entity.toDomain().getOrNull()
                    }
                }
                accountRepository.saveMany(domainAccounts)
            }
            val budgets = async { budgetWriter.saveMany(completeData.budgets) }
            val categories =
                async { categoryWriter.saveMany(completeData.categories) }
            accounts.await()
            budgets.await()
            categories.await()

            onProgress(0.7)

            val loans = async { loanWriter.saveMany(completeData.loans) }
            val loanRecords =
                async { loanRecordWriter.saveMany(completeData.loanRecords) }

            loans.await()
            loanRecords.await()

            onProgress(0.8)

            val tags = async { tagsWriter.save(completeData.tags) }
            val tagAssociations = async { tagAssociationWriter.save(completeData.tagAssociations) }

            val plannedPayments =
                async { plannedPaymentRuleWriter.saveMany(completeData.plannedPaymentRules) }
            val settings = async {
                settingsWriter.deleteAll()
                settingsWriter.saveMany(completeData.settings)
            }

            sharedPrefs.putBoolean(
                SharedPrefs.SHOW_NOTIFICATIONS,
                (completeData.sharedPrefs[SharedPrefs.SHOW_NOTIFICATIONS] ?: "true").toBoolean()
            )

            sharedPrefs.putBoolean(
                SharedPrefs.APP_LOCK_ENABLED,
                (completeData.sharedPrefs[SharedPrefs.APP_LOCK_ENABLED] ?: "false").toBoolean()
            )

            sharedPrefs.putBoolean(
                SharedPrefs.HIDE_CURRENT_BALANCE,
                (completeData.sharedPrefs[SharedPrefs.HIDE_CURRENT_BALANCE] ?: "false").toBoolean()
            )

            sharedPrefs.putBoolean(
                SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE,
                (
                        completeData.sharedPrefs[SharedPrefs.TRANSFERS_AS_INCOME_EXPENSE]
                            ?: "false"
                        ).toBoolean()
            )

            plannedPayments.await()
            settings.await()
            tags.await()
            tagAssociations.await()

            onProgress(0.9)
        }
    }

    /** This is used to replace account & category Ids in backup data with existing Ids
     *  This removes the problem of duplicate Accounts & Categories
     *
     *  returns a Pair<A,B> of IDs where A is the UUID that needs to be replaced with B
     */
    private suspend fun getReplacementPairs(
        completeData: IvyWalletCompleteData
    ): List<Pair<UUID, UUID>> {
        return withContext(dispatchersProvider.io) {
            val existingAccountsList = accountDao.findAll()
            val existingCategoryList = categoryDao.findAll()

            val backupAccountsList = completeData.accounts
            val backupCategoryList = completeData.categories

            if (existingAccountsList.isEmpty() && existingCategoryList.isEmpty()) {
                return@withContext emptyList()
            }

            val sumAccountList = existingAccountsList + backupAccountsList
            val sumCategoriesList = existingCategoryList + backupCategoryList

            val accountsReplace = async {
                sumAccountList.groupBy { it.name }.filter { it.value.size == 2 }.map {
                    val accountsZero = it.value[0]
                    val accountsFirst = it.value[1]

                    if (backupAccountsList.contains(accountsZero)) {
                        Pair(accountsZero.id, accountsFirst.id)
                    } else {
                        Pair(accountsFirst.id, accountsZero.id)
                    }
                }
            }

            val categoriesReplace = async {
                sumCategoriesList.groupBy { it.name }.filter { it.value.size == 2 }.map {
                    val categoryZero = it.value[0]
                    val categoryFirst = it.value[1]

                    if (completeData.categories.contains(categoryZero)) {
                        Pair(categoryZero.id, categoryFirst.id)
                    } else {
                        Pair(categoryFirst.id, categoryZero.id)
                    }
                }
            }

            accountsReplace.await() + categoriesReplace.await()
        }
    }

    private fun hasJsonExtension(file: File): Boolean {
        val name = file.name
        val lastIndexOf = name.lastIndexOf(".")
        if (lastIndexOf == -1) {
            return false
        }

        return (name.substring(lastIndexOf).equals(".json", true))
    }

    private fun clearCacheDir() {
        context.cacheDir.deleteRecursively()
    }
}
