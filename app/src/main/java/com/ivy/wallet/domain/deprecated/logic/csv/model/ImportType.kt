package com.ivy.wallet.domain.deprecated.logic.csv.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ivy.wallet.R
import com.ivy.wallet.ui.csvimport.flow.instructions.*
import com.ivy.wallet.ui.theme.*

enum class ImportType {
    IVY,
    MONEY_MANAGER,
    WALLET_BY_BUDGET_BAKERS,
    SPENDEE,
    MONEFY,
    ONE_MONEY,
    BLUE_COINS,
    KTW_MONEY_MANAGER,
    FORTUNE_CITY,
    FINANCISTO;

    fun color(): Color = when (this) {
        IVY -> Ivy
        MONEY_MANAGER -> Red
        WALLET_BY_BUDGET_BAKERS -> Green
        SPENDEE -> RedLight
        MONEFY -> Green
        ONE_MONEY -> Red3
        BLUE_COINS -> Blue
        KTW_MONEY_MANAGER -> Yellow
        FORTUNE_CITY -> Green2Light
        FINANCISTO -> White
    }

    fun appId(): String = when (this) {
        IVY -> "com.ivy.wallet"
        MONEY_MANAGER -> "com.realbyteapps.moneymanagerfree"
        WALLET_BY_BUDGET_BAKERS -> "com.droid4you.application.wallet"
        SPENDEE -> "com.cleevio.spendee"
        MONEFY -> "com.monefy.app.lite"
        ONE_MONEY -> "org.pixelrush.moneyiq"
        BLUE_COINS -> "com.rammigsoftware.bluecoins"
        KTW_MONEY_MANAGER -> "com.ktwapps.walletmanager"
        FORTUNE_CITY -> "com.fourdesire.fortunecity"
        FINANCISTO -> "ru.orangesoftware.financisto"
    }

    @DrawableRes
    fun logo(): Int = when (this) {
        IVY -> R.drawable.ivywallet_logo
        MONEY_MANAGER -> R.drawable.moneymanager_logo
        WALLET_BY_BUDGET_BAKERS -> R.drawable.wallet_by_budgetbakers_logo
        SPENDEE -> R.drawable.spendee_logo
        MONEFY -> R.drawable.monefy_logo
        ONE_MONEY -> R.drawable.one_money_logo
        BLUE_COINS -> R.drawable.bluecoins
        KTW_MONEY_MANAGER -> R.drawable.ktw_money_manager_logo
        FORTUNE_CITY -> R.drawable.fortune_city_app_logo
        FINANCISTO -> R.drawable.financisto_logo
    }

    fun listName(): String = when (this) {
        IVY -> "Ivy Wallet"
        MONEY_MANAGER -> "Money Manager"
        WALLET_BY_BUDGET_BAKERS -> "Wallet by BudgetBakers"
        SPENDEE -> "Spendee"
        MONEFY -> "Monefy"
        ONE_MONEY -> "1Money"
        BLUE_COINS -> "Bluecoins Finance"
        KTW_MONEY_MANAGER -> "Money Manager (KTW)"
        FORTUNE_CITY -> "Fortune City"
        FINANCISTO -> "Financisto"
    }

    fun appName(): String = when (this) {
        IVY -> "Ivy Wallet"
        else -> listName()
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
            MONEY_MANAGER -> {
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
            MONEFY -> MonefySteps(
                onUploadClick = onUploadClick
            )
            ONE_MONEY -> OneMoneySteps(
                onUploadClick = onUploadClick
            )
            BLUE_COINS -> DefaultImportSteps(
                onUploadClick = onUploadClick
            )
            KTW_MONEY_MANAGER -> KTWMoneyManagerSteps(
                onUploadClick = onUploadClick
            )
            FORTUNE_CITY -> FortuneCitySteps(
                onUploadClick = onUploadClick
            )
            FINANCISTO -> FinancistoSteps(
                onUploadClick = onUploadClick
            )
        }
    }
}