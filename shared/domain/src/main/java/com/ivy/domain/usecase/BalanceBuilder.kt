package com.ivy.domain.usecase

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.data.model.primitive.PositiveDouble

class BalanceBuilder {

    private val balance = mutableMapOf<AssetCode, NonZeroDouble>()

    fun processIncomes(
        incomes: Map<AssetCode, PositiveDouble>,
        transferIn: Map<AssetCode, PositiveDouble>,
    ) {
        combineAndGetAmount(incomes, transferIn).forEach { (asset, value) ->
            NonZeroDouble
                .from((balance[asset]?.value ?: 0.0) + value.value)
                .onRight { newValue ->
                    balance[asset] = newValue
                }
        }
    }

    fun processOutcomes(
        expenses: Map<AssetCode, PositiveDouble>,
        transferOut: Map<AssetCode, PositiveDouble>,
    ) {
        combineAndGetAmount(expenses, transferOut).forEach { (asset, value) ->
            NonZeroDouble
                .from((balance[asset]?.value ?: 0.0) + (-value.value))
                .onRight { newValue ->
                    balance[asset] = newValue
                }
        }
    }

    private fun combineAndGetAmount(
        firstMap: Map<AssetCode, PositiveDouble>,
        secondMap: Map<AssetCode, PositiveDouble>,
    ): Map<AssetCode, PositiveDouble> {
        val amount = firstMap.toMutableMap()
        secondMap.forEach { (asset, value) ->
            amount.merge(asset, value) { firstMap, secondMap ->
                PositiveDouble
                    .from(firstMap.value + secondMap.value)
                    .fold(
                        ifLeft = { PositiveDouble.unsafe(0.0) },
                        ifRight = { it }
                    )
            }
        }
        return amount
    }

    fun build(): Map<AssetCode, NonZeroDouble> = balance
}