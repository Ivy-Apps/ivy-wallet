package com.ivy.wallet.domain.deprecated.sync.item

import com.ivy.wallet.domain.deprecated.sync.uploader.AccountUploader
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.utils.timeNowUTC
import com.ivy.wallet.utils.toEpochSeconds

class AccountSync(
    private val sharedPrefs: SharedPrefs,
    private val dao: AccountDao,
    restClient: RestClient,
    private val uploader: AccountUploader,
    private val ivySession: IvySession
) {
    private val service = restClient.accountService

    suspend fun isSynced(): Boolean =
        dao.findByIsSyncedAndIsDeleted(synced = false, deleted = false).isEmpty() &&
                dao.findByIsSyncedAndIsDeleted(synced = false, deleted = true).isEmpty()

    suspend fun sync() {
        if (!ivySession.isLoggedIn()) return

        val syncStart = timeNowUTC().toEpochSeconds()

        uploadUpdated()
        deleteDeleted()
        fetchNew()

        sharedPrefs.putLong(SharedPrefs.LAST_SYNC_DATE_ACCOUNTS, syncStart)
    }

    private suspend fun uploadUpdated() {
        val toSync = dao.findByIsSyncedAndIsDeleted(
            synced = false,
            deleted = false
        )

        for (item in toSync) {
            uploader.sync(item.toDomain())
        }
    }

    private suspend fun deleteDeleted() {
        val toDelete = dao.findByIsSyncedAndIsDeleted(
            synced = false,
            deleted = true
        )

        for (item in toDelete) {
            uploader.delete(item.id)
        }
    }

    private suspend fun fetchNew() {
        try {
            val afterTimestamp = sharedPrefs.getEpochSeconds(SharedPrefs.LAST_SYNC_DATE_ACCOUNTS)

            val response = service.get(after = afterTimestamp)

            response.accounts.forEach { item ->
                dao.save(
                    item.toEntity().copy(
                        isSynced = true,
                        isDeleted = false
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}