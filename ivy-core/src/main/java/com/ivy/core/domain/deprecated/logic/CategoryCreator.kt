package com.ivy.wallet.domain.deprecated.logic

import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.domain.pure.util.nextOrderNum
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.utils.ioThread

class CategoryCreator(
    private val paywallLogic: PaywallLogic,
    private val categoryDao: CategoryDao,
) {
    suspend fun createCategory(
        data: CreateCategoryData,
        onRefreshUI: suspend (Category) -> Unit
    ) {
        val name = data.name
        if (name.isBlank()) return

        try {
            paywallLogic.protectAddWithPaywall(
                addCategory = true,
            ) {
                val newCategory = ioThread {
                    val newCategory = Category(
                        name = name.trim(),
                        color = data.color.toArgb(),
                        icon = data.icon,
                        orderNum = categoryDao.findMaxOrderNum().nextOrderNum(),
                        isSynced = false
                    )

                    categoryDao.save(newCategory.toEntity())
                    newCategory
                }

                onRefreshUI(newCategory)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun editCategory(
        updatedCategory: Category,
        onRefreshUI: suspend (Category) -> Unit
    ) {
        if (updatedCategory.name.isBlank()) return

        try {
            ioThread {
                categoryDao.save(
                    updatedCategory.toEntity().copy(
                        isSynced = false
                    )
                )
            }

            onRefreshUI(updatedCategory)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
