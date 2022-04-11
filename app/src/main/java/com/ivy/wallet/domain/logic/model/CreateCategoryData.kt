package com.ivy.wallet.domain.logic.model

import androidx.compose.ui.graphics.Color

data class CreateCategoryData(
    val name: String,
    val color: Color,
    val icon: String?
)