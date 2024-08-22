package com.ivy.domain.usecase

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.data.model.primitive.PositiveDouble

class BalanceBuilder {

    private val balance = mutableMapOf<AssetCode, NonZeroDouble>()

    /**
     * Updates `balance` with deposits from income and transfers in.
     *
     * @param incomes Deposit amounts from income sources.
     * @param transferIn Deposit amounts from transfer in sources.
     */
    fun processDeposits(
        incomes: Map<AssetCode, PositiveDouble>,
        transferIn: Map<AssetCode, PositiveDouble>,
    ) {
        combine(incomes, transferIn).forEach { (asset, amount) ->
            NonZeroDouble
                .from((balance[asset]?.value ?: 0.0) + amount.value)
                .onRight { newValue ->
                    balance[asset] = newValue
                }
        }
    }

    /**
     * Updates `balance` with withdrawals from expenses and transfers out.
     *
     * @param expenses Deposit amounts from expense sources.
     * @param transferOut Deposit amounts from transfer out sources.
     */
    fun processWithdrawals(
        expenses: Map<AssetCode, PositiveDouble>,
        transferOut: Map<AssetCode, PositiveDouble>,
    ) {
        combine(expenses, transferOut).forEach { (asset, amount) ->
            NonZeroDouble
                .from((balance[asset]?.value ?: 0.0) - amount.value)
                .onRight { newValue ->
                    balance[asset] = newValue
                }
        }
    }

    /**
     * Combines two maps by summing their values. If the sum results in an error,
     * the value from the `c` map is used.
     *
     * Note: `c` is a copy of `a`.
     *
     * @return A map with combined values from the two input maps.
     */
    private fun combine(
        a: Map<AssetCode, PositiveDouble>,
        b: Map<AssetCode, PositiveDouble>,
    ): Map<AssetCode, PositiveDouble> {
        val c = a.toMutableMap()
        b.forEach { (asset, amount) ->
            c.merge(asset, amount) { b, c ->
                PositiveDouble
                    .from(b.value + c.value)
                    .fold(
                        ifLeft = { c },
                        ifRight = { it }
                    )
            }
        }
        return c
    }

    fun build(): Map<AssetCode, NonZeroDouble> = balance
}