package com.ivy.domain.usecase

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.data.model.primitive.PositiveDouble

class BalanceBuilder {

    private val balance = mutableMapOf<AssetCode, NonZeroDouble>()

    fun processDeposit(
        incomes: Map<AssetCode, PositiveDouble>,
        transferIn: Map<AssetCode, PositiveDouble>,
    ) {
        getAmount(incomes, transferIn).forEach { (asset, value) ->
            NonZeroDouble
                .from((balance[asset]?.value ?: 0.0) + value.value)
                .onRight { newValue ->
                    balance[asset] = newValue
                }
        }
    }

    fun processWithdrawal(
        expenses: Map<AssetCode, PositiveDouble>,
        transferOut: Map<AssetCode, PositiveDouble>,
    ) {
        getAmount(expenses, transferOut).forEach { (asset, value) ->
            NonZeroDouble
                .from((balance[asset]?.value ?: 0.0) + (-value.value))
                .onRight { newValue ->
                    balance[asset] = newValue
                }
        }
    }

    private fun getAmount(
        a: Map<AssetCode, PositiveDouble>,
        b: Map<AssetCode, PositiveDouble>,
    ): Map<AssetCode, PositiveDouble> {
        val amount = a.toMutableMap()
        combine(amount, b)
        return amount
    }

    private fun combine(
        a: MutableMap<AssetCode, PositiveDouble>,
        b: Map<AssetCode, PositiveDouble>
    ) {
        b.forEach { (asset, value) ->
            a.merge(asset, value) { firstMap, secondMap ->
                PositiveDouble
                    .from(firstMap.value + secondMap.value)
                    .fold(
                        ifLeft = { PositiveDouble.unsafe(0.0) },
                        ifRight = { it }
                    )
            }
        }
    }

    fun build(): Map<AssetCode, NonZeroDouble> = balance
}