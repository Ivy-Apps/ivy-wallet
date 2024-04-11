package com.ivy.domain.model

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonNegativeInt
import com.ivy.data.model.primitive.PositiveDouble

data class StatSummary(
    val values: Map<AssetCode, PositiveDouble>,
    val trnCount: NonNegativeInt,
) {
    companion object {
        val Zero = StatSummary(
            values = emptyMap(),
            trnCount = NonNegativeInt.Zero
        )
    }
}