package com.ivy.formula.domain.data.project

import androidx.compose.ui.graphics.Color
import com.ivy.data.ItemIconId

data class ProjectInfo(
    val name: String,
    val description: String,
    val color: Color,
    val iconId: ItemIconId
)