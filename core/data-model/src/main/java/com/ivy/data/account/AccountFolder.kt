package com.ivy.data.account

import com.ivy.data.ItemIconId

data class AccountFolder(
    val name: String,
    val icon: ItemIconId?,
    val color: Int,
    val orderNum: Double,
    val accounts: List<Account>,
)