package com.ivy.core.functions.account

import androidx.annotation.ColorInt
import com.ivy.core.functions.sync.dummySync
import com.ivy.data.CurrencyCode
import com.ivy.data.IvyIcon
import com.ivy.data.SyncMetadata
import com.ivy.data.account.AccMetadata
import com.ivy.data.account.Account
import java.util.*

fun dummyAcc(
    id: UUID = UUID.randomUUID(),
    name: String = "Dummy acc",
    currency: CurrencyCode = "USD",
    @ColorInt
    color: Int = 1,
    icon: IvyIcon? = null,
    excluded: Boolean = false,
    metadata: AccMetadata = dummyAccMetadata(),
): Account = Account(
    id = id,
    name = name,
    currency = currency,
    color = color,
    icon = icon,
    excluded = excluded,
    metadata = metadata
)

fun dummyAccMetadata(
    orderNum: Double = 0.0,
    sync: SyncMetadata = dummySync()
): AccMetadata = AccMetadata(
    orderNum = orderNum,
    sync = sync
)