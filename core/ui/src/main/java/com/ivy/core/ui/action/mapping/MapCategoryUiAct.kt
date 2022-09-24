package com.ivy.core.ui.action.mapping

import com.ivy.core.ui.action.DefaultTo
import com.ivy.core.ui.action.IconAct
import com.ivy.core.ui.data.CategoryUi
import com.ivy.data.category.Category
import com.ivy.design.l0_system.color.toComposeColor
import javax.inject.Inject

class MapCategoryUiAct @Inject constructor(
    private val iconAct: IconAct
) : MapUiAction<Category, CategoryUi>() {
    override suspend fun transform(domain: Category): CategoryUi = CategoryUi(
        id = domain.id.toString(),
        name = domain.name,
        icon = iconAct(IconAct.Input(iconId = domain.icon, defaultTo = DefaultTo.Category)),
        color = domain.color.toComposeColor()
    )
}