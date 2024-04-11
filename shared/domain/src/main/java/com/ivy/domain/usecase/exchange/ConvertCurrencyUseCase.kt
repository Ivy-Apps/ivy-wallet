package com.ivy.domain.usecase.exchange

import com.ivy.data.model.Value
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.repository.ExchangeRatesRepository
import javax.inject.Inject

class ConvertCurrencyUseCase @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository
) {

    fun exchange(value: Value, into: AssetCode): Value {
        TODO()
    }
}