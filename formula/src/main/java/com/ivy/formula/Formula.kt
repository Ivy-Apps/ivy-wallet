package com.ivy.formula

import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

typealias Input = Flow<Double>

sealed interface InputItem {
    data class F(val formula: Formula) : InputItem
    data class Const(val value: Flow<Double>) : InputItem
}

abstract class Formula(
    val input: NonEmptyList<Input>
) {
    abstract fun formula(input: NonEmptyList<Input>): Flow<Double>

    operator fun invoke(): Flow<Double> = formula(input)
}

fun const(value: Double): Input = flowOf(value)

fun percent(value: Double): Double = value / 100

fun args(
    input: NonEmptyList<Input>,
    f: (args: Array<Double>) -> Double
): Flow<Double> = combine(*input.toTypedArray()) { f(it) }

// region Test 1
suspend fun test() {
    /*
        input = [const 20k]
        " = $1" => "$1"
     */
    val f1 = object : Formula(
        input = nonEmptyListOf(const(20_000.0))
    ) {
        override fun formula(input: NonEmptyList<Input>): Flow<Double> = args(input) {
            it[0]
        }
    }

    val parseF1 = parse(
        input = nonEmptyListOf(InputItem.Const(const(20_000.0))),
        formula = "$1"
    )

    /*
        input = [f1, const 80]
        " = $1 * $2%" => "$1*$2%
     */
    val f2 = object : Formula(
        input = nonEmptyListOf(f1(), const(80.0))
    ) {
        override fun formula(input: NonEmptyList<Input>): Flow<Double> = args(input) {
            it[0] * percent(it[1])
        }
    }

    val parseF2 = parse(
        input = nonEmptyListOf(InputItem.F(parseF1), InputItem.Const(const(80.0))),
        formula = "$1*$2%"
    )
}

suspend fun parse(
    input: NonEmptyList<InputItem>,
    formula: String
): Formula {
    object : Formula(
        input = input.map {
            when (it) {
                is InputItem.Const -> it.value
                is InputItem.F -> it.formula.invoke()
            }
        }
    ) {
        override fun formula(input: NonEmptyList<Input>): Flow<Double> = args(input) {
            TODO("Not yet implemented")
        }
    }
    TODO()
}
// endregion