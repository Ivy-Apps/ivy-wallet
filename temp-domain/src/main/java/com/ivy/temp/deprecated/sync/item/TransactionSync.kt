package com.ivy.wallet.domain.deprecated.sync.item

import com.ivy.wallet.domain.deprecated.sync.uploader.TransactionUploader
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.persistence.SharedPrefs
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.utils.timeNowUTC
import com.ivy.wallet.utils.toEpochSeconds

class TransactionSync(
    private val sharedPrefs: SharedPrefs,
    private val dao: TransactionDao,
    restClient: RestClient,
    private val uploader: TransactionUploader,
    private val ivySession: IvySession
) {
    private val service = restClient.transactionService

    suspend fun isSynced(): Boolean =
        dao.findByIsSyncedAndIsDeleted(synced = false, deleted = false).isEmpty() &&
                dao.findByIsSyncedAndIsDeleted(synced = false, deleted = true).isEmpty()

    suspend fun sync() {
        if (!ivySession.isLoggedIn()) return

        val syncStart = timeNowUTC().toEpochSeconds()

        uploadUpdated()
        deleteDeleted()
        fetchNew()

        sharedPrefs.putLong(SharedPrefs.LAST_SYNC_DATE_TRANSACTIONS, syncStart)
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
            val afterTimestamp =
                sharedPrefs.getEpochSeconds(SharedPrefs.LAST_SYNC_DATE_TRANSACTIONS)

            val response = service.get(after = afterTimestamp)

            response.transactions.forEach { item ->
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