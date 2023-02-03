package com.ivy.core.data.common

/**
 * Int that is **>=0** and in the range [0, [Int.MAX_VALUE]].
 * Use [NonNegativeInt.of] or [Int.asPositive] to create one.
 */
@JvmInline
value class NonNegativeInt private constructor(val value: Int) {
    companion object {
        /**
         * @throws error if the [Int] isn't positive: **this >= 0**
         * @return a valid [NonNegativeInt]
         */
        fun of(value: Int): NonNegativeInt = if (value >= 0.0) NonNegativeInt(value) else
            error("PositiveInt error: $value is not a non-negative (>= 0) number.")
    }
}


/**
 * See [NonNegativeInt.of].
 */
fun Int.asNonNegative(): NonNegativeInt = NonNegativeInt.of(this)


/**
 * Int that is **>0** and in the range [1, [Int.MAX_VALUE]].
 * Use [PositiveInt.of] or [Int.asPositive] to create one.
 */
@JvmInline
value class PositiveInt private constructor(val value: Int) {
    companion object {
        /**
         * @throws error if the [Int] isn't positive: **this > 0**
         * @return a valid [PositiveInt]
         */
        fun of(value: Int): PositiveInt = if (value > 0.0) PositiveInt(value) else
            error("PositiveInt error: $value is not a positive (> 0) number.")
    }
}

/**
 * See [PositiveInt.of].
 */
fun Int.asPositive(): PositiveInt = PositiveInt.of(this)


/**
 * Double that is **>=0** and in the range [0, [Double.MAX_VALUE]].
 * Use [NonNegativeDouble.of] or [Double.asNonNegative] to create one.
 */
@JvmInline
value class NonNegativeDouble private constructor(val value: Double) {
    companion object {
        /**
         * @throws error if the [Double] isn't positive: **this >= 0**
         * @return a valid [NonNegativeDouble]
         */
        fun of(value: Double): NonNegativeDouble = if (value >= 0.0) NonNegativeDouble(value) else
            error("NonNegativeDouble error: $value is not a non-negative (>= 0) number.")
    }
}

/**
 * See [NonNegativeDouble.of].
 */
fun Double.asNonNegative(): NonNegativeDouble = NonNegativeDouble.of(this)


/**
 * Double that is **>0** and in the range (0, [Double.MAX_VALUE]].
 * Use [PositiveDouble.of] or [Double.asPositive] to create one.
 */
@JvmInline
value class PositiveDouble private constructor(val value: Double) {
    companion object {
        /**
         * @throws error if the [Double] isn't positive: **this > 0**
         * @return a valid [PositiveDouble]
         */
        fun of(value: Double): PositiveDouble = if (value > 0.0) PositiveDouble(value) else
            error("PositiveDouble error: $value is not a positive (> 0) number.")
    }
}

/**
 * See [PositiveDouble.of].
 */
fun Double.asPositive(): PositiveDouble = PositiveDouble.of(this)