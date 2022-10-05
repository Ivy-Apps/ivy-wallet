package com.ivy.formula.domain.source

import com.ivy.data.CurrencyCode

/**
 * @param outputCurrency use **null** for base currency
 */
data class Calculation(
    val thing: CalculationThing,
    val type: CalculationType,
    val outputCurrency: CurrencyCode?,
)