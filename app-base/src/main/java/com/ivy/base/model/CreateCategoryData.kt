package com.ivy.wallet.domain.deprecated.logic.model

import androidx.compose.ui.graphics.Color
import com.ivy.data.CategoryOld

data class CreateCategoryData(
    val name: String,
    val color: Color,
    val icon: String?,
    val parentCategory: CategoryOld? = null
)