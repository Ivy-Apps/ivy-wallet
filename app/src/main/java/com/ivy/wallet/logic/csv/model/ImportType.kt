package com.ivy.wallet.logic.csv.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ivy.wallet.R
import com.ivy.wallet.ui.csvimport.flow.instructions.*
import com.ivy.wallet.ui.theme.*

enum class ImportType {
    IVY,
    MONEY_MANAGER_PRASE,
    WALLET_BY_BUDGET_BAKERS,
    SPENDEE,
    ONE_MONEY,
    KTW_MONEY_MANAGER,
    FORTUNE_CITY;

    fun color(): Color = when (this) {
        IVY -> Ivy
        MONEY_MANAGER_PRASE -> Red
        WALLET_BY_BUDGET_BAKERS -> Green
        SPENDEE -> RedLight
        ONE_MONEY -> Red3
        KTW_MONEY_MANAGER -> Yellow
        FORTUNE_CITY -> Green2Light
    }

    fun appId(): String = when (this) {
        IVY -> "com.ivy.wallet"
        MONEY_MANAGER_PRASE -> "com.realbyteapps.moneymanagerfree"
        WALLET_BY_BUDGET_BAKERS -> "com.droid4you.application.wallet"
        SPENDEE -> "com.cleevio.spendee"
        ONE_MONEY -> "org.pixelrush.moneyiq"
        KTW_MONEY_MANAGER -> "com.ktwapps.walletmanager"
        FORTUNE_CITY -> "com.fourdesire.fortunecity"
    }

    @DrawableRes
    fun logo(): Int = when (this) {
        IVY -> R.drawable.ivy_wallet_logo_import
        MONEY_MANAGER_PRASE -> R.drawable.ic_money_manager_prase
        WALLET_BY_BUDGET_BAKERS -> R.drawable.wallet_by_budgetbakers_logo
        SPENDEE -> R.drawable.speende_logo_png
        ONE_MONEY -> R.drawable.one_money_logo
        KTW_MONEY_MANAGER -> R.drawable.ktw_money_manager_logo
        FORTUNE_CITY -> R.drawable.fortune_city_app_logo
    }

    fun listName(): String = when (this) {
        IVY -> "Ivy Wallet CSV"
        MONEY_MANAGER_PRASE -> "Money Manager"
        WALLET_BY_BUDGET_BAKERS -> "Wallet by BudgetBakers"
        SPENDEE -> "Spendee"
        ONE_MONEY -> "1Money"
        KTW_MONEY_MANAGER -> "Money Manager (KTW)"
        FORTUNE_CITY -> "Fortune City"
    }

    fun appName(): String = when (this) {
        IVY -> "Ivy Wallet"
        MONEY_MANAGER_PRASE -> "Money Manager"
        WALLET_BY_BUDGET_BAKERS -> "Wallet by BudgetBakers"
        SPENDEE -> "Spendee"
        ONE_MONEY -> "1Money"
        KTW_MONEY_MANAGER -> "Money Manager (KTW)"
        FORTUNE_CITY -> "Fortune City"
    }

    @Composable
    fun ImportSteps(
        onUploadClick: () -> Unit
    ) {
        when (this) {
            IVY -> {
                IvyWalletSteps(
                    onUploadClick = onUploadClick
                )
            }
            MONEY_MANAGER_PRASE -> {
                MoneyManagerPraseSteps(
                    onUploadClick = onUploadClick
                )
            }
            WALLET_BY_BUDGET_BAKERS -> {
                WalletByBudgetBakersSteps(
                    onUploadClick = onUploadClick
                )
            }
            SPENDEE -> SpendeeSteps(
                onUploadClick = onUploadClick
            )
            ONE_MONEY -> OneMoneySteps(
                onUploadClick = onUploadClick
            )
            KTW_MONEY_MANAGER -> KTWMoneyManagerSteps(
                onUploadClick = onUploadClick
            )
            FORTUNE_CITY -> FortuneCitySteps(
                onUploadClick = onUploadClick
            )
        }
    }
}