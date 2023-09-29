package com.ivy.wallet.domain.deprecated.logic

import androidx.compose.ui.graphics.toArgb
import com.ivy.legacy.datamodel.Category
import com.ivy.legacy.utils.ioThread
import com.ivy.data.db.dao.read.CategoryDao
import com.ivy.data.db.dao.write.WriteCategoryDao
import com.ivy.wallet.domain.deprecated.logic.model.CreateCategoryData
import com.ivy.wallet.domain.pure.util.nextOrderNum
import javax.inject.Inject

class CategoryCreator @Inject constructor(
    private val categoryDao: CategoryDao,
    private val categoryWriter: WriteCategoryDao,
) {
    suspend fun createCategory(
        data: CreateCategoryData,
        onRefreshUI: suspend (Category) -> Unit
    ) {
        val name = data.name
        if (name.isBlank()) return

        try {
            val newCategory = ioThread {
                val newCategory = Category(
                    name = name.trim(),
                    color = data.color.toArgb(),
                    icon = data.icon,
                    orderNum = categoryDao.findMaxOrderNum().nextOrderNum(),
                    isSynced = false
                )

                categoryWriter.save(newCategory.toEntity())
                newCategory
            }

            onRefreshUI(newCategory)
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
                categoryWriter.save(
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
