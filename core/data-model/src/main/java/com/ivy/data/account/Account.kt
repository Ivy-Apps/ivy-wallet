package com.ivy.data.account

import androidx.annotation.ColorInt
import com.ivy.data.CurrencyCode
import com.ivy.data.IvyIconId
import com.ivy.data.SyncState
import java.util.*

data class Account(
    val id: UUID,
    val name: String,
    val currency: CurrencyCode,
    @ColorInt
    val color: Int,
    val icon: IvyIconId?,
    val excluded: Boolean,
    val folderId: UUID?,
    val orderNum: Double,
    val state: AccountState,
    val sync: SyncState
)