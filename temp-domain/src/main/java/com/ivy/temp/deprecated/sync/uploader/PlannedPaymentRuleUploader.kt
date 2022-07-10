package com.ivy.wallet.domain.deprecated.sync.uploader

import com.ivy.data.planned.PlannedPaymentRule
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.network.data.toDTO
import com.ivy.wallet.io.network.request.planned.DeletePlannedPaymentRuleRequest
import com.ivy.wallet.io.network.request.planned.UpdatePlannedPaymentRuleRequest
import com.ivy.wallet.io.persistence.dao.PlannedPaymentRuleDao
import com.ivy.wallet.io.persistence.data.toEntity
import timber.log.Timber
import java.util.*

class PlannedPaymentRuleUploader(
    private val dao: PlannedPaymentRuleDao,
    restClient: RestClient,
    private val ivySession: IvySession
) {
    private val service = restClient.plannedPaymentRuleService

    suspend fun sync(item: PlannedPaymentRule) {
        if (!ivySession.isLoggedIn()) return

        try {
            //update
            service.update(
                UpdatePlannedPaymentRuleRequest(
                    rule = item.toDTO()
                )
            )

            //flag as synced
            dao.save(
                item.copy(
                    isSynced = true
                ).toEntity()
            )
            Timber.d("PlannedPaymentRule updated: $item.")
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
                DeletePlannedPaymentRuleRequest(
                    id = id
                )
            )

            //delete from local db
            dao.deleteById(id)
            Timber.d("PlannedPaymentRule deleted: $id.")
        } catch (e: Exception) {
            Timber.e("Failed to delete with error (${e.message}): $id")
            e.printStackTrace()

            //delete from local db
            dao.deleteById(id)
        }
    }

}