package com.ivy.data.account

import com.ivy.data.ItemIconId

data class AccountFolder(
    val id: String,
    val name: String,
    val icon: ItemIconId?,
    val color: Int,
    val orderNum: Double,
    val accounts: List<Account>,
)