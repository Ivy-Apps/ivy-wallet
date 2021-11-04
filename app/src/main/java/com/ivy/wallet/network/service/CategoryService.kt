package com.ivy.wallet.network.service

import com.ivy.wallet.network.request.category.DeleteWalletCategoryRequest
import com.ivy.wallet.network.request.category.UpdateWalletCategoryRequest
import com.ivy.wallet.network.request.category.WalletCategoriesResponse
import retrofit2.http.*

interface CategoryService {
    @POST("/wallet/categories/update")
    suspend fun update(@Body request: UpdateWalletCategoryRequest)

    @GET("/wallet/categories")
    suspend fun get(@Query("after") after: Long? = null): WalletCategoriesResponse

    @HTTP(method = "DELETE", path = "/wallet/categories/delete", hasBody = true)
    suspend fun delete(@Body request: DeleteWalletCategoryRequest)
}