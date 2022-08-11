package com.ivy.data.account

import com.ivy.data.SyncMetadata

data class AccMetadata(
    val orderNum: Double,
    val sync: SyncMetadata
)