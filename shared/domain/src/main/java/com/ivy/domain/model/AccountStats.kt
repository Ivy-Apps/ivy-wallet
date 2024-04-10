package com.ivy.domain.model

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonNegativeInt
import com.ivy.data.model.primitive.PositiveDouble

data class AccountStats(
    val income: Summary,
    val expense: Summary,
    val transfersIn: Summary,
    val transfersOut: Summary,
) {
    companion object {
        val Zero = AccountStats(
            income = Summary.Zero,
            expense = Summary.Zero,
            transfersIn = Summary.Zero,
            transfersOut = Summary.Zero,
        )
    }
}

data class Summary(
    val values: Map<AssetCode, PositiveDouble>,
    val trnCount: NonNegativeInt,
) {
    companion object {
        val Zero = Summary(
            values = emptyMap(),
            trnCount = NonNegativeInt.Zero
        )
    }
}