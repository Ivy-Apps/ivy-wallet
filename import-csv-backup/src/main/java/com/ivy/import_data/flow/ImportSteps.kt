package com.ivy.import_data.flow

import androidx.compose.runtime.Composable
import com.ivy.import_data.flow.instructions.*
import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportApp
import com.ivy.wallet.ui.csvimport.flow.instructions.MoneyWalletSteps

@Composable
fun ImportSteps(
    type: ImportApp,
    onUploadClick: () -> Unit
) {
    when (type) {
        ImportApp.IVY -> {
            IvyWalletSteps(
                onUploadClick = onUploadClick
            )
        }
        ImportApp.MONEY_MANAGER -> {
            MoneyManagerPraseSteps(
                onUploadClick = onUploadClick
            )
        }
        ImportApp.WALLET_BY_BUDGET_BAKERS -> {
            WalletByBudgetBakersSteps(
                onUploadClick = onUploadClick
            )
        }
        ImportApp.SPENDEE -> SpendeeSteps(
            onUploadClick = onUploadClick
        )
        ImportApp.MONEFY -> MonefySteps(
            onUploadClick = onUploadClick
        )
        ImportApp.ONE_MONEY -> OneMoneySteps(
            onUploadClick = onUploadClick
        )
        ImportApp.BLUE_COINS -> DefaultImportSteps(
            onUploadClick = onUploadClick
        )
        ImportApp.KTW_MONEY_MANAGER -> KTWMoneyManagerSteps(
            onUploadClick = onUploadClick
        )
        ImportApp.FORTUNE_CITY -> FortuneCitySteps(
            onUploadClick = onUploadClick
        )
        ImportApp.FINANCISTO -> FinancistoSteps(
            onUploadClick = onUploadClick
        )
        ImportApp.MONEY_WALLET -> MoneyWalletSteps(
            onUploadClick = onUploadClick
        )
    }
}