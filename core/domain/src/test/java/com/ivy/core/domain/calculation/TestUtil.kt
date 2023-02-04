package com.ivy.core.domain.calculation

import java.text.DecimalFormat

fun Double.round(): String = DecimalFormat("0.00").format(this)