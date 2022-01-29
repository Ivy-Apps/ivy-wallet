package com.ivy.wallet.logic

import androidx.compose.ui.graphics.toArgb
import com.ivy.wallet.base.ioThread
import com.ivy.wallet.logic.model.CreateCategoryData
import com.ivy.wallet.model.entity.Category
import com.ivy.wallet.persistence.dao.CategoryDao
import com.ivy.wallet.sync.uploader.CategoryUploader

class CategoryCreator(
    private val paywallLogic: PaywallLogic,
    private val categoryDao: CategoryDao,
    private val categoryUploader: CategoryUploader
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
                        orderNum = categoryDao.findMaxOrderNum() + 1,
                        isSynced = false
                    )

                    categoryDao.
                    save(newCategory)
                    newCategory
                }

                onRefreshUI(newCategory)

                ioThread {
                    categoryUploader.sync(newCategory)
                }
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
                    updatedCategory.copy(
                        isSynced = false
                    )
                )
            }

            onRefreshUI(updatedCategory)

            ioThread {
                categoryUploader.sync(updatedCategory)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}