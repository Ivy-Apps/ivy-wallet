package com.ivy.wallet.domain.deprecated.sync.uploader

import com.ivy.wallet.domain.data.core.Budget
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.network.request.budget.CrupdateBudgetRequest
import com.ivy.wallet.io.network.request.budget.DeleteBudgetRequest
import com.ivy.wallet.io.persistence.dao.BudgetDao
import timber.log.Timber
import java.util.*

class BudgetUploader(
    private val dao: BudgetDao,
    restClient: RestClient,
    private val ivySession: IvySession
) {
    private val service = restClient.budgetService

    suspend fun sync(item: Budget) {
        if (!ivySession.isLoggedIn()) return

        try {
            //update
            service.update(
                CrupdateBudgetRequest(
                    budget = item.toDTO()
                )
            )

            //flag as synced
            dao.save(
                item.copy(
                    isSynced = true
                ).toEntity()
            )
            Timber.d("Budget updated: $item.")
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
                DeleteBudgetRequest(
                    id = id
                )
            )

            //delete from local db
            dao.deleteById(id)
            Timber.d("Budget deleted: $id.")
        } catch (e: Exception) {
            Timber.e("Failed to delete with error (${e.message}): $id")
            e.printStackTrace()

            //delete from local db
            dao.deleteById(id)
        }
    }

}