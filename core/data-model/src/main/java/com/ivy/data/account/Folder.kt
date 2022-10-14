package com.ivy.data.account

import com.ivy.data.ItemIconId

data class Folder(
    val id: String,
    val name: String,
    val icon: ItemIconId?,
    val color: Int,
    val orderNum: Double,
)