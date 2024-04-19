package com.ivy.domain.model

import arrow.core.Option
import com.ivy.data.model.PositiveValue
import com.ivy.data.model.primitive.AssetCode

data class ExchangeResult(
    /**
     * Some value that was exchanged successfully.
     * Or [arrow.core.None] if there were errors or an empty map was exchanged.
     */
    val exchanged: Option<PositiveValue>,
    /**
     * Assets for which the exchange has failed.
     */
    val errors: Set<AssetCode>,
)