package com.ivy.wallet.domain.deprecated.sync.uploader

import com.ivy.data.loan.LoanRecord
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.network.data.toDTO
import com.ivy.wallet.io.network.request.loan.DeleteLoanRecordRequest
import com.ivy.wallet.io.network.request.loan.UpdateLoanRecordRequest
import com.ivy.wallet.io.persistence.dao.LoanRecordDao
import com.ivy.wallet.io.persistence.data.toEntity
import timber.log.Timber
import java.util.*

class LoanRecordUploader(
    private val dao: LoanRecordDao,
    restClient: RestClient,
    private val ivySession: IvySession
) {
    private val service = restClient.loanService

    suspend fun sync(item: LoanRecord) {
        if (!ivySession.isLoggedIn()) return

        try {
            //update
            service.updateRecord(
                UpdateLoanRecordRequest(
                    loanRecord = item.toDTO()
                )
            )

            //flag as synced
            dao.save(
                item.copy(
                    isSynced = true
                ).toEntity()
            )
            Timber.d("Loan record updated: $item.")
        } catch (e: Exception) {
            Timber.e("Failed to update with error (${e.message}): $item")
            e.printStackTrace()
        }
    }


    suspend fun delete(id: UUID) {
        if (!ivySession.isLoggedIn()) return

        try {
            //Delete on server
            service.deleteRecord(
                DeleteLoanRecordRequest(
                    id = id
                )
            )

            //delete from local db
            dao.deleteById(id)
            Timber.d("Loan record deleted: $id.")
        } catch (e: Exception) {
            Timber.e("Failed to delete with error (${e.message}): $id")
            e.printStackTrace()

            //delete from local db
            dao.deleteById(id)
        }
    }

}