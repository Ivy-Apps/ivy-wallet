package com.ivy.wallet.sync.uploader

import com.ivy.wallet.model.entity.Loan
import com.ivy.wallet.network.RestClient
import com.ivy.wallet.network.request.loan.DeleteLoanRequest
import com.ivy.wallet.network.request.loan.UpdateLoanRequest
import com.ivy.wallet.persistence.dao.LoanDao
import com.ivy.wallet.session.IvySession
import timber.log.Timber
import java.util.*

class LoanUploader(
    private val dao: LoanDao,
    restClient: RestClient,
    private val ivySession: IvySession
) {
    private val service = restClient.loanService

    suspend fun sync(item: Loan) {
        if (!ivySession.isLoggedIn()) return

        try {
            //update
            service.update(
                UpdateLoanRequest(
                    loan = item
                )
            )

            //flag as synced
            dao.save(
                item.copy(
                    isSynced = true
                )
            )
            Timber.d("Loan updated: $item.")
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
                DeleteLoanRequest(
                    id = id
                )
            )

            //delete from local db
            dao.deleteById(id)
            Timber.d("Loan deleted: $id.")
        } catch (e: Exception) {
            Timber.e("Failed to delete with error (${e.message}): $id")
            e.printStackTrace()

            //delete from local db
            dao.deleteById(id)
        }
    }

}