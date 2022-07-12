package com.ivy.import_data.flow

import androidx.compose.runtime.Composable
import com.ivy.import_data.flow.instructions.*
import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportType
import com.ivy.wallet.ui.csvimport.flow.instructions.MoneyWalletSteps

@Composable
fun ImportSteps(
    type: ImportType,
    onUploadClick: () -> Unit
) {
    when (type) {
        ImportType.IVY -> {
            IvyWalletSteps(
                onUploadClick = onUploadClick
            )
        }
        ImportType.MONEY_MANAGER -> {
            MoneyManagerPraseSteps(
                onUploadClick = onUploadClick
            )
        }
        ImportType.WALLET_BY_BUDGET_BAKERS -> {
            WalletByBudgetBakersSteps(
                onUploadClick = onUploadClick
            )
        }
        ImportType.SPENDEE -> SpendeeSteps(
            onUploadClick = onUploadClick
        )
        ImportType.MONEFY -> MonefySteps(
            onUploadClick = onUploadClick
        )
        ImportType.ONE_MONEY -> OneMoneySteps(
            onUploadClick = onUploadClick
        )
        ImportType.BLUE_COINS -> DefaultImportSteps(
            onUploadClick = onUploadClick
        )
        ImportType.KTW_MONEY_MANAGER -> KTWMoneyManagerSteps(
            onUploadClick = onUploadClick
        )
        ImportType.FORTUNE_CITY -> FortuneCitySteps(
            onUploadClick = onUploadClick
        )
        ImportType.FINANCISTO -> FinancistoSteps(
            onUploadClick = onUploadClick
        )
        ImportType.MONEY_WALLET -> MoneyWalletSteps(
            onUploadClick = onUploadClick
        )
    }
}