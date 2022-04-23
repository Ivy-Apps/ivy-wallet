package com.ivy.wallet.domain.deprecated.sync.uploader

import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.network.request.account.DeleteAccountRequest
import com.ivy.wallet.io.network.request.account.UpdateAccountRequest
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import timber.log.Timber
import java.util.*

class AccountUploader(
    private val accountDao: AccountDao,
    private val transactionDao: TransactionDao,
    restClient: RestClient,
    private val ivySession: IvySession
) {
    private val service = restClient.accountService

    suspend fun sync(item: Account) {
        if (!ivySession.isLoggedIn()) return

        try {
            //update
            service.update(
                UpdateAccountRequest(
                    account = item.toDTO()
                )
            )

            //flag as synced
            accountDao.save(
                item.copy(
                    isSynced = true
                ).toEntity()
            )
            Timber.d("Account updated: $item.")
        } catch (e: Exception) {
            Timber.e("Failed to update with error (${e.message}): $item")
            e.printStackTrace()
        }
    }


    suspend fun delete(id: UUID) {
        if (!ivySession.isLoggedIn()) return

        try {
            //Delete on server
            service.delete(
                DeleteAccountRequest(
                    id = id
                )
            )

            //delete from local db
            transactionDao.deleteAllByAccountId(id)
            accountDao.deleteById(id)
            Timber.d("Account deleted: $id.")
        } catch (e: Exception) {
            Timber.e("Failed to delete with error (${e.message}): $id")
            e.printStackTrace()

            //delete from local db
            transactionDao.deleteAllByAccountId(id)
            accountDao.deleteById(id)
        }
    }

}