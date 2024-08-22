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
        firstMap: Map<AssetCode, PositiveDouble>,
        secondMap: Map<AssetCode, PositiveDouble>,
    ): Map<AssetCode, PositiveDouble> {
        val amount = firstMap.toMutableMap()
        combine(amount, secondMap)
        return amount
    }

    private fun combine(
        amount: MutableMap<AssetCode, PositiveDouble>,
        secondMap: Map<AssetCode, PositiveDouble>
    ) {
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
    }

    fun build(): Map<AssetCode, NonZeroDouble> = balance
}