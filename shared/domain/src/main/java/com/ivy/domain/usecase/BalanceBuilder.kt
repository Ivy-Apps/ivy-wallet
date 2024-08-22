package com.ivy.domain.usecase

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.data.model.primitive.PositiveDouble

class BalanceBuilder {

    private val balance = mutableMapOf<AssetCode, NonZeroDouble>()

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

    private fun combine(
        a: Map<AssetCode, PositiveDouble>,
        b: Map<AssetCode, PositiveDouble>
    ): Map<AssetCode, PositiveDouble> {
        val c = a.toMutableMap()
        b.forEach { (asset, amount) ->
            c.merge(asset, amount) { firstMap, secondMap ->
                PositiveDouble
                    .from(firstMap.value + secondMap.value)
                    .fold(
                        ifLeft = { PositiveDouble.unsafe(0.0) },
                        ifRight = { it }
                    )
            }
        }
        return c
    }

    fun build(): Map<AssetCode, NonZeroDouble> = balance
}