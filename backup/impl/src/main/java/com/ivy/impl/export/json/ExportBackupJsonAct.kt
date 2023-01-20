package com.ivy.impl.export.json

import com.ivy.common.time.provider.TimeProvider
import com.ivy.common.time.toUtc
import com.ivy.core.domain.action.Action
import com.ivy.core.persistence.dao.AttachmentDao
import com.ivy.core.persistence.dao.account.AccountDao
import com.ivy.core.persistence.dao.account.AccountFolderDao
import com.ivy.core.persistence.dao.category.CategoryDao
import com.ivy.core.persistence.dao.exchange.ExchangeRateOverrideDao
import com.ivy.core.persistence.dao.tag.TagDao
import com.ivy.core.persistence.dao.trn.TransactionDao
import com.ivy.core.persistence.dao.trn.TrnLinkRecordDao
import com.ivy.core.persistence.dao.trn.TrnMetadataDao
import com.ivy.core.persistence.dao.trn.TrnTagDao
import com.ivy.core.persistence.datastore.IvyDataStore
import com.ivy.core.persistence.datastore.keys.SettingsKeys
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.json.JSONObject
import javax.inject.Inject

/**
 * Exports all Ivy Wallet's data as a JSON.
 */
class ExportBackupJsonAct @Inject constructor(
    private val accountDao: AccountDao,
    private val accountFolderDao: AccountFolderDao,
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao,
    private val trnTagDao: TrnTagDao,
    private val tagDao: TagDao,
    private val trnLinkRecordDao: TrnLinkRecordDao,
    private val trnMetadataDao: TrnMetadataDao,
    private val attachmentDao: AttachmentDao,
    private val exchangeRateOverrideDao: ExchangeRateOverrideDao,
    private val dataStore: IvyDataStore,
    private val settingsKeys: SettingsKeys,
    private val timeProvider: TimeProvider,
) : Action<Unit, JSONObject>() {

    // it's only CPU work, computational work => use Dispatchers.Default instead of IO
    override fun dispatcher() = Dispatchers.Default

    override suspend fun action(input: Unit): JSONObject {
        return coroutineScope {
            val accounts = async { exportAccountsJson(accountDao) }
            val accountFolders = async { exportAccountFoldersJson(accountFolderDao) }
            val categories = async { exportCategoriesJson(categoryDao) }
            val tags = async { exportTagsToJson(tagDao) }
            val attachments = async { exportAttachmentsJson(attachmentDao) }
            val transactions = async { exportTransactionsJson(transactionDao) }
            val trnMetadata = async { exportTrnMetadataJson(trnMetadataDao) }
            val trnLinks = async { exportTrnLinkRecordsJson(trnLinkRecordDao) }
            val trnTags = async { exportTrnTagsJson(trnTagDao) }
            val exchangeRatesOverrides =
                async { exportExchangeRatesOverridesToJson(exchangeRateOverrideDao) }
            val settings = async { exportSettingsToJson(dataStore, settingsKeys) }

            JSONObject().apply {
                put("backupInfo", backupInfo())
                put("accounts", accounts.await())
                put("accountFolders", accountFolders.await())
                put("categories", categories.await())
                put("tags", tags.await())
                put("attachments", attachments.await())
                put("transactions", transactions.await())
                put("trnMetadata", trnMetadata.await())
                put("trnLinks", trnLinks.await())
                put("trnTags", trnTags.await())
                put("exchangeRatesOverrides", exchangeRatesOverrides.await())
                put("settings", settings.await())
            }
        }
    }

    private fun backupInfo(): JSONObject {
        return JSONObject().apply {
            put("version", 1)
            put("timestamp", timeProvider.timeNow().toUtc(timeProvider).epochSecond)
        }
    }
}