package com.ivy.math

import java.text.DecimalFormat

/**
 * Formats number like a calculator would do.
 * Precision is set to 6 decimals.
 */
fun formatNumber(number: Double): String =
    DecimalFormat("###,###,##0.${"#".repeat(6)}").format(number)