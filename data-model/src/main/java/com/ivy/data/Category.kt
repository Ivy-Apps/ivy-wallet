package com.ivy.data

import java.util.*

data class Category(
    val name: String,
    val color: Int,
    val icon: String? = null,
    val orderNum: Double = 0.0,
    val parentCategoryId: UUID? = null,

    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    val id: UUID = UUID.randomUUID()
)