package com.ivy.wallet.ui.charts

enum class ChartType {
    GENERAL, CATEGORY, ACCOUNT;

    fun display(): String {
        return when (this) {
            GENERAL -> "General"
            CATEGORY -> "Categories"
            ACCOUNT -> "Accounts"
        }
    }
}