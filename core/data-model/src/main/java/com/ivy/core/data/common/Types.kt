package com.ivy.core.data.common

/**
 * Int that is **>0** and in the range [1, [Int.MAX_VALUE]].
 * Use [PositiveInt.new] or [Int.asPositive] to create one.
 */
@JvmInline
value class PositiveInt private constructor(val value: Int) {
    companion object {
        /**
         * @throws error if the [Int] isn't positive: **this > 0**
         * @return a valid [PositiveInt]
         */
        fun new(value: Int): PositiveInt = if (value > 0.0) PositiveInt(value) else
            error("PositiveInt error: $value is not a positive number.")
    }
}

/**
 * @throws error if the [Int] isn't positive: **this > 0**
 * @return a valid [PositiveInt]
 */
fun Int.asPositive(): PositiveInt = PositiveInt.new(this)


/**
 * Double that is **>0** and in the range (0, [Double.MAX_VALUE]].
 * Use [PositiveDouble.new] or [Double.asPositive] to create one.
 */
@JvmInline
value class PositiveDouble private constructor(val value: Double) {
    companion object {
        /**
         * @throws error if the [Double] isn't positive: **this > 0**
         * @return a valid [PositiveDouble]
         */
        fun new(value: Double): PositiveDouble = if (value > 0.0) PositiveDouble(value) else
            error("PositiveDouble error: $value is not a positive number.")
    }
}

/**
 * @throws error if the [Double] isn't positive: **this > 0**
 * @return a valid [PositiveDouble]
 */
fun Double.asPositive(): PositiveDouble = PositiveDouble.new(this)