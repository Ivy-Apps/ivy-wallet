package com.ivy.wallet.sync.uploader

import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.network.RestClient
import com.ivy.wallet.network.request.category.DeleteWalletCategoryRequest
import com.ivy.wallet.network.request.category.UpdateWalletCategoryRequest
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.session.IvySession
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
                    category = item
                )
            )

            //flag as synced
            dao.save(
                item.copy(
                    isSynced = true
                )
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