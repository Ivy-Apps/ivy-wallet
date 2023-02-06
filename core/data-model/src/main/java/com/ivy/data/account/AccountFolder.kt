package com.ivy.data.account

import com.ivy.data.ItemIconId
import com.ivy.data.Sync

@Deprecated("will be removed!")
data class AccountFolder(
    val id: String,
    val name: String,
    val icon: ItemIconId?,
    val color: Int,
    val orderNum: Double,
    val sync: Sync,
)