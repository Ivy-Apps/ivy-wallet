package com.ivy.wallet.sync.uploader

import com.ivy.wallet.model.entity.Account
import com.ivy.wallet.network.RestClient
import com.ivy.wallet.network.request.account.DeleteAccountRequest
import com.ivy.wallet.network.request.account.UpdateAccountRequest
import com.ivy.wallet.persistence.dao.AccountDao
import com.ivy.wallet.persistence.dao.TransactionDao
import com.ivy.wallet.session.IvySession
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
                    account = item
                )
            )

            //flag as synced
            accountDao.save(
                item.copy(
                    isSynced = true
                )
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