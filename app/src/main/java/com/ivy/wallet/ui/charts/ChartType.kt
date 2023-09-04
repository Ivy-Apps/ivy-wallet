package com.ivy.wallet.ui.charts

enum class ChartType {
    WALLET, CATEGORY, ACCOUNT;

    fun display(): String {
        return when (this) {
            WALLET -> "Wallet"
            CATEGORY -> "Categories"
            ACCOUNT -> "Accounts"
        }
    }
}
