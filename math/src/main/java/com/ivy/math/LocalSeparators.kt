package com.ivy.math

import java.text.DecimalFormatSymbols

fun localDecimalSeparator(): Char =
    DecimalFormatSymbols.getInstance().decimalSeparator

fun localGroupingSeparator(): Char =
    DecimalFormatSymbols.getInstance().groupingSeparator