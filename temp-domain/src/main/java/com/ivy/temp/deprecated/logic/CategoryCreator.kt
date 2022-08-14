package com.ivy.wallet.domain.deprecated.logic

import androidx.compose.ui.graphics.toArgb
import com.ivy.data.CategoryOld
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.domain.deprecated.sync.uploader.CategoryUploader
import com.ivy.wallet.domain.pure.util.nextOrderNum
import com.ivy.wallet.io.persistence.dao.CategoryDao
import com.ivy.wallet.io.persistence.data.toEntity
import com.ivy.wallet.utils.ioThread
import javax.inject.Inject

class CategoryCreator @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryUploader: CategoryUploader
) {
    suspend fun createCategory(
        data: CreateCategoryData,
        onRefreshUI: suspend (CategoryOld) -> Unit
    ) {
        val name = data.name
        if (name.isBlank()) return

        try {

            val newCategory = ioThread {
                val newCategory = CategoryOld(
                    name = name.trim(),
                    color = data.color.toArgb(),
                    icon = data.icon,
                    orderNum = categoryDao.findMaxOrderNum().nextOrderNum(),
                    isSynced = false,
                    parentCategoryId = data.parentCategory?.id
                )

                categoryDao.save(newCategory.toEntity())
                newCategory
            }

            onRefreshUI(newCategory)

            ioThread {
                categoryUploader.sync(newCategory)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun editCategory(
        updatedCategory: CategoryOld,
        onRefreshUI: suspend (CategoryOld) -> Unit
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

            ioThread {
                categoryUploader.sync(updatedCategory)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}