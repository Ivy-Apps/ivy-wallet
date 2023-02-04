package com.ivy.core.domain.calculation

import com.ivy.core.data.common.AssetCode
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.string

fun Arb.Companion.assetCode(): Arb<AssetCode> = arbitrary {
    AssetCode.fromStringUnsafe(
        Arb.string(minSize = 1, maxSize = 12)
            .filter { it.isNotBlank() }.bind()
    )
}