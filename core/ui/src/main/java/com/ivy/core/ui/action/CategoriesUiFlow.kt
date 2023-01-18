package com.ivy.core.ui.action

import android.content.Context
import com.ivy.core.domain.action.SharedFlowAction
import com.ivy.core.domain.action.category.CategoriesFlow
import com.ivy.core.ui.data.CategoryUi
import com.ivy.design.l0_system.color.toComposeColor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoriesUiFlow @Inject constructor(
    @ApplicationContext
    private val appContext: Context,
    private val categoriesFlow: CategoriesFlow,
) : SharedFlowAction<Map<String, CategoryUi>?>() {
    override fun initialValue(): Map<String, CategoryUi>? = null

    override fun createFlow(): Flow<Map<String, CategoryUi>?> = categoriesFlow().map { cats ->
        cats.associate {
            val id = it.id.toString()
            id to CategoryUi(
                id = id,
                name = it.name,
                color = it.color.toComposeColor(),
                icon = itemIcon(
                    appContext = appContext,
                    iconId = it.icon,
                    defaultTo = DefaultTo.Account,
                ),
                hasParent = it.parentCategoryId != null
            )
        }
    }
}