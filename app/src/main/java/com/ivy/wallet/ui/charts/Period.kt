package com.ivy.wallet.ui.charts

enum class Period {
    LAST_12_MONTHS,
    LAST_6_MONTHS,
    LAST_4_WEEKS,
    LAST_7_DAYS;

    fun display(): String {
        return when (this) {
            LAST_12_MONTHS -> "Last 12 months"
            LAST_6_MONTHS -> "Last 6 months"
            LAST_4_WEEKS -> "Last 4 weeks"
            LAST_7_DAYS -> "Last 7 days"
        }
    }
}