package com.ivy.core.data.common

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.toOption

/**
 * Int that is **>=0** and in the range [0, [Int.MAX_VALUE]].
 * Use [NonNegativeInt.fromIntUnsafe] or [Int.toNonNegativeUnsafe] to create one.
 */
@JvmInline
value class NonNegativeInt private constructor(val value: Int) {
    companion object {
        /**
         * @throws error if the [Int] isn't positive: **this >= 0**
         * @return a valid [NonNegativeInt]
         */
        fun fromIntUnsafe(value: Int): NonNegativeInt = fromInt(value).getOrElse {
            error("PositiveInt error: $value is not a non-negative (>= 0) number.")
        }

        fun fromInt(value: Int): Option<NonNegativeInt> =
            value.takeIf { it >= 0.0 }.toOption().map(::NonNegativeInt)
    }
}


/**
 * See [NonNegativeInt.fromInt] and [NonNegativeInt.fromIntUnsafe].
 */
fun Int.toNonNegativeUnsafe(): NonNegativeInt = NonNegativeInt.fromIntUnsafe(this)
fun Int.toNonNegative(): Option<NonNegativeInt> = NonNegativeInt.fromInt(this)


/**
 * Int that is **>0** and in the range [1, [Int.MAX_VALUE]].
 * Use [PositiveInt.fromIntUnsafe] or [Int.toPositiveUnsafe] to create one.
 */
@JvmInline
value class PositiveInt private constructor(val value: Int) {
    companion object {
        /**
         * @throws error if the [Int] isn't positive: **this > 0**
         * @return a valid [PositiveInt]
         */
        fun fromIntUnsafe(value: Int): PositiveInt = fromInt(value).getOrElse {
            error("PositiveInt error: $value is not a positive (> 0) number.")
        }

        fun fromInt(value: Int): Option<PositiveInt> =
            value.takeIf { it > 0.0 }.toOption().map(::PositiveInt)
    }
}

/**
 * See [PositiveInt.fromIntUnsafe].
 */
fun Int.toPositiveUnsafe(): PositiveInt = PositiveInt.fromIntUnsafe(this)
fun Int.toPositive(): Option<PositiveInt> = PositiveInt.fromInt(this)


/**
 * Double that is **>=0** and in the range [0, [Double.MAX_VALUE]].
 * Use [NonNegativeDouble.fromDoubleUnsafe] or [Double.toNonNegativeUnsafe] to create one.
 */
@JvmInline
value class NonNegativeDouble private constructor(val value: Double) {
    companion object {
        /**
         * @throws error if the [Double] isn't positive: **this >= 0**
         * @return a valid [NonNegativeDouble]
         */
        fun fromDoubleUnsafe(value: Double): NonNegativeDouble =
            fromDouble(value).getOrElse {
                error("NonNegativeDouble error: $value is not a non-negative (>= 0) number.")
            }

        fun fromDouble(value: Double): Option<NonNegativeDouble> =
            value.takeIf { it >= 0.0 }.toOption().map(::NonNegativeDouble)
    }
}

/**
 * See [NonNegativeDouble.fromDoubleUnsafe].
 */
fun Double.toNonNegativeUnsafe(): NonNegativeDouble = NonNegativeDouble.fromDoubleUnsafe(this)
fun Double.toNonNegative(): Option<NonNegativeDouble> = NonNegativeDouble.fromDouble(this)


/**
 * Double that is **>0** and in the range (0, [Double.MAX_VALUE]].
 * Use [PositiveDouble.fromDoubleUnsafe] or [Double.toPositiveUnsafe] to create one.
 */
@JvmInline
value class PositiveDouble private constructor(val value: Double) {
    companion object {
        /**
         * @throws error if the [Double] isn't positive: **this > 0**
         * @return a valid [PositiveDouble]
         */
        fun fromDoubleUnsafe(value: Double): PositiveDouble = fromDouble(value).getOrElse {
            error("PositiveDouble error: $value is not a positive (> 0) number.")
        }

        fun fromDouble(value: Double): Option<PositiveDouble> =
            value.takeIf { it > 0.0 }.toOption().map(::PositiveDouble)

    }
}

/**
 * See [PositiveDouble.fromDoubleUnsafe].
 */
fun Double.toPositiveUnsafe(): PositiveDouble = PositiveDouble.fromDoubleUnsafe(this)
fun Double.toPositive(): Option<PositiveDouble> = PositiveDouble.fromDouble(this)