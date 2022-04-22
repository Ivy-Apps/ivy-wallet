package com.ivy.wallet.domain.deprecated.logic

import com.ivy.wallet.domain.data.core.Category
import com.ivy.wallet.domain.logic.model.CreateCategoryData
import com.ivy.wallet.domain.sync.uploader.CategoryUploader
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.utils.ioThread

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