package com.ivy.domain.usecase.exchange

import arrow.core.Option
import com.ivy.data.model.PositiveValue
import com.ivy.data.model.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.data.repository.ExchangeRatesRepository
import com.ivy.domain.model.ExchangeResult
import javax.inject.Inject

class ExchangeUseCase @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository,
) {
    /**
     * Converts a [value] from one currency [to] another.
     *
     * @return some value if the conversion was successful or [arrow.core.None]
     */
    suspend fun convert(value: PositiveValue, to: AssetCode): Option<PositiveValue> {
        TODO("Not implemented")
    }

    /**
     * @return none exchanged value for empty [values] or if the
     * exchange fails for all [AssetCode]
     */
    suspend fun convert(
        values: Map<AssetCode, NonZeroDouble>,
        to: AssetCode
    ): ExchangeResult {
        TODO("Not implemented")
    }

    suspend fun convert(
        value: Value,
        to: AssetCode
    ): Option<Value> {
        TODO("Not implemented")
    }
}