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
     * @param transfersIn Deposit amounts from transfer in sources.
     */
    fun processDeposits(
        incomes: Map<AssetCode, PositiveDouble>,
        transfersIn: Map<AssetCode, PositiveDouble>,
    ) {
        combine(incomes, transfersIn).forEach { (asset, amount) ->
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
     * @param transfersOut Deposit amounts from transfer out sources.
     */
    fun processWithdrawals(
        expenses: Map<AssetCode, PositiveDouble>,
        transfersOut: Map<AssetCode, PositiveDouble>,
    ) {
        combine(expenses, transfersOut).forEach { (asset, amount) ->
            val sub = (balance[asset]?.value ?: 0.0) - amount.value
            NonZeroDouble
                .from(sub)
                .onLeft {
                    if (sub == 0.0) {
                        balance.remove(asset)
                    }
                }
                .onRight { newValue ->
                    balance[asset] = newValue
                }
        }
    }

    /**
     * Combines two maps by summing their values. If the sum results in an error,
     * the value from the `a` map is used.
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