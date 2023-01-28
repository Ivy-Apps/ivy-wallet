package com.ivy.core.data.common

/**
 * Int that is **>0** and in the range [1, [Int.MAX_VALUE]].
 * Use [Int.asPositive] to create one.
 */
@JvmInline
value class PositiveInt(val int: Int)

/**
 * @throws error if the [Int] isn't positive: **this > 0**
 * @return a valid [PositiveInt]
 */
fun Int.asPositive(): PositiveInt =
    if (this > 0.0) PositiveInt(this) else
        error("PositiveInt error: $this is not a positive number.")


/**
 * Double that is **>0** and in the range (0, [Double.MAX_VALUE]].
 * Use [Double.asPositive] to create one.
 */
@JvmInline
value class PositiveDouble internal constructor(val double: Double)

/**
 * @throws error if the [Double] isn't positive: **this > 0**
 * @return a valid [PositiveDouble]
 */
fun Double.asPositive(): PositiveDouble =
    if (this > 0.0) PositiveDouble(this) else
        error("PositiveDouble error: $this is not a positive number.")


/**
 * An int between 1 and 31 inclusively representing a date in a month.
 * Use [Int.asMonthDate] to create one.
 */
@JvmInline
value class MonthDate internal constructor(val int: Int)

/**
 * @throws error if the int isn't between 1 and 31
 * @return a valid [MonthDate]
 */
fun Int.asMonthDate(): MonthDate = if (this in 1..31)
    MonthDate(this) else error("MonthDate error: $this is not a valid date in a month")