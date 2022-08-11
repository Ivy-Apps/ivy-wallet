package com.ivy.sync.category

import com.ivy.data.category.Category
import com.ivy.frp.action.FPAction
import com.ivy.frp.asParamTo
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.thenIfSuccess
import com.ivy.frp.monad.tryOp
import com.ivy.frp.thenInvokeAfter
import com.ivy.temp.persistence.IOEffect
import com.ivy.temp.persistence.mapToEntity
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.request.category.DeleteWalletCategoryRequest
import com.ivy.wallet.io.network.request.category.UpdateWalletCategoryRequest
import com.ivy.wallet.io.network.service.CategoryService
import com.ivy.wallet.io.persistence.dao.CategoryDao
import javax.inject.Inject

class SyncCategoryAct @Inject constructor(
    private val categoryDao: CategoryDao,
    private val ivySession: IvySession,
    private val categoryService: CategoryService
) : FPAction<IOEffect<Category>, Unit>() {
    override suspend fun IOEffect<Category>.compose(): suspend () -> Unit = {
        sync(this)
    }

    private suspend fun sync(operation: IOEffect<Category>) {
        if (!ivySession.isLoggedIn()) return

        when (operation) {
            is IOEffect.Delete -> delete(operation.item)
            is IOEffect.Save -> save(operation.item)
        }
    }

    private suspend fun delete(item: Category) = tryOp(
        operation = DeleteWalletCategoryRequest(id = item.id) asParamTo categoryService::delete
    ) thenInvokeAfter {
        categoryDao.deleteById(item.id)
    }

    private suspend fun save(item: Category) = tryOp(
        operation = UpdateWalletCategoryRequest(
            category = mapToDTO(item)
        ) asParamTo categoryService::update
    ) thenIfSuccess {
        val syncedItem = item.mark(
            isSynced = true, isDeleted = false
        )
        persist(syncedItem)
        Res.Ok(Unit)
    } thenInvokeAfter {}

    private suspend fun persist(item: Category) {
        categoryDao.save(mapToEntity(item))
    }
}