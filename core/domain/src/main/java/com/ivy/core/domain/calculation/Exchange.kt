package com.ivy.core.domain.calculation

import arrow.core.Option
import arrow.core.continuations.option
import arrow.core.toOption
import com.ivy.core.data.calculation.ExchangeRates
import com.ivy.core.data.common.AssetCode
import com.ivy.core.data.common.NonNegativeDouble
import com.ivy.core.data.common.PositiveDouble
import com.ivy.core.data.common.toPositiveUnsafe

/**
 * Exchanges an [amount] in [from] asset to [to] asset.
 *
 * @return Some successfully exchanged amount or None
 */
suspend fun ExchangeRates.exchange(
    amount: NonNegativeDouble,
    from: AssetCode,
    to: AssetCode
): Option<NonNegativeDouble> = option {
    if (from == to) return@option amount // no need to exchange
    if (amount.value == 0.0) return@option amount // no need to exchange

    val rate = findRate(from, to).bind()
    NonNegativeDouble.fromDoubleUnsafe(rate.value * amount.value)
}

suspend fun ExchangeRates.findRate(
    from: AssetCode,
    to: AssetCode,
): Option<PositiveDouble> {
    fun rate(asset: AssetCode): Option<PositiveDouble> =
        rates[asset].toOption()

    return option {
        if (from == to) return@option 1.0.toPositiveUnsafe() // no need to exchange

        when (base) {
            from -> {
                /*
                Case: BGN -> EUR
                base = from = "BGN"
                to = "EUR"
                1 BGN (from) = 1 * 0.51 (rate to) = 0.51 (rate to) = 0.51 EUR
                 */
                rate(to).bind()
            }

            to -> {
                /*
                Case: EUR -> BGN
                base = to = "BGN"
                from = "EUR"

                1 EUR (from) = 1 / 0.51 (rate from) = 1.96 BGN (to)
                 */
                val rateBaseFrom = rate(from).bind()
                PositiveDouble.fromDouble(1.0 / rateBaseFrom.value).bind()
            }

            else -> {
                /*
                Case: EUR -> USD
                base = "BGN"
                from = "EUR"
                to = "USD"

                1 EUR = 1 / 0.51 (rate from) = 1.96 BGN
                1 USD = 1 / 0.56 (rate to) = 1.8 BGN

                1 EUR = 1.96 BGN / 1.8 BGN =
                = [1 / 0.51 (rate from)] / [1 / 0.56 (rate to)] = 1.08 USD
                => 1 EUR = 0.56 (rate to) / 0.51 (rate from) = 1.08 USD
                 */
                val rateFrom = rate(from).bind()
                val rateTo = rate(to).bind()
                PositiveDouble.fromDouble(rateTo.value / rateFrom.value).bind()
            }
        }
    }
}