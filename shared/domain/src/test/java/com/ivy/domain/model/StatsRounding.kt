package com.ivy.domain.model

import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.model.util.VALUE_DECIMAL_PLACES_PRECISION
import com.ivy.data.model.util.roundTo

fun StatSummary.round(): StatSummary = StatSummary(
    trnCount = trnCount,
    values = values.mapValues { (_, amount) ->
        PositiveDouble.unsafe(amount.value.roundTo(VALUE_DECIMAL_PLACES_PRECISION))
    }
)