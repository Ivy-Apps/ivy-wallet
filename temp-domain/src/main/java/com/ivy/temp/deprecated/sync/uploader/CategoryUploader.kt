package com.ivy.wallet.domain.deprecated.sync.uploader

import com.ivy.data.Category
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.RestClient
import com.ivy.wallet.io.network.data.toDTO
import com.ivy.wallet.io.network.request.category.DeleteWalletCategoryRequest
import com.ivy.wallet.io.network.request.category.UpdateWalletCategoryRequest
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.data.toEntity
import timber.log.Timber
import java.util.*

class CategoryUploader(
    private val dao: CategoryDao,
    restClient: RestClient,
    private val ivySession: IvySession
) {
    private val service = restClient.categoryService

    suspend fun sync(item: Category) {
        if (!ivySession.isLoggedIn()) return

        try {
            //update
            service.update(
                UpdateWalletCategoryRequest(
                    category = item.toDTO()
                )
            )

            //flag as synced
            dao.save(
                item.copy(
                    isSynced = true
                ).toEntity()
            )
            Timber.d("Category updated: $item.")
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
                DeleteWalletCategoryRequest(
                    id = id
                )
            )

            //delete from local db
            dao.deleteById(id)
            Timber.d("Category deleted: $id.")
        } catch (e: Exception) {
            Timber.e("Failed to delete with error (${e.message}): $id")
            e.printStackTrace()

            //delete from local db
            dao.deleteById(id)
        }
    }

}