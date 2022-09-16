package com.ivy.sync.ivyserver.category

import com.ivy.data.category.Category
import com.ivy.frp.asParamTo
import com.ivy.frp.monad.Res
import com.ivy.frp.monad.mapSuccess
import com.ivy.frp.monad.tryOp
import com.ivy.frp.thenInvokeAfter
import com.ivy.sync.base.SyncItem
import com.ivy.wallet.io.network.IvySession
import com.ivy.wallet.io.network.data.CategoryDTO
import com.ivy.wallet.io.network.request.category.DeleteWalletCategoryRequest
import com.ivy.wallet.io.network.request.category.UpdateWalletCategoryRequest
import com.ivy.wallet.io.network.service.CategoryService
import javax.inject.Inject

class CategoryIvyServerSync @Inject constructor(
    private val ivySession: IvySession,
    private val categoryService: CategoryService
) : SyncItem<Category> {
    override suspend fun enabled(): SyncItem<Category>? = this.takeIf { ivySession.isLoggedIn() }

    override suspend fun save(items: List<Category>): List<Category> = items.map { saveItem(it) }
        .mapNotNull { if (it is Res.Ok) it.data else null }

    private suspend fun saveItem(item: Category): Res<Exception, Category> = tryOp(
        operation = UpdateWalletCategoryRequest(
            category = mapToDTO(item)
        ) asParamTo categoryService::update
    ) mapSuccess { item } thenInvokeAfter { it }

    override suspend fun delete(items: List<Category>): List<Category> =
        items.map { deleteItem(it) }
            .mapNotNull { if (it is Res.Ok) it.data else null }

    private suspend fun deleteItem(item: Category): Res<Exception, Category> = tryOp(
        operation = DeleteWalletCategoryRequest(
            id = item.id
        ) asParamTo categoryService::delete
    ) mapSuccess { item } thenInvokeAfter { it }

    override suspend fun get(): Res<Unit, List<Category>> {
        TODO("Not yet implemented")
    }

    private fun mapToDTO(cat: Category): CategoryDTO = TODO()
}