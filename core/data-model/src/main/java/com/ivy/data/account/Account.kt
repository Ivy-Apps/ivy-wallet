package com.ivy.data.account

import androidx.annotation.ColorInt
import com.ivy.data.CurrencyCode
import com.ivy.data.ItemIconId
import com.ivy.data.Sync
import java.util.*

@Deprecated("will be removed!")
data class Account(
    val id: UUID,
    val name: String,
    val currency: CurrencyCode,
    @ColorInt
    val color: Int,
    val icon: ItemIconId?,
    val excluded: Boolean,
    val folderId: UUID?,
    val orderNum: Double,
    val state: AccountState,
    val sync: Sync,
)