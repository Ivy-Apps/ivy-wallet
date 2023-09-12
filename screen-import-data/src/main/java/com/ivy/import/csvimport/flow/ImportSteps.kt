package com.ivy.import.csvimport.flow

import androidx.compose.runtime.Composable
import com.ivy.import.csvimport.flow.instructions.DefaultImportSteps
import com.ivy.import.csvimport.flow.instructions.FinancistoSteps
import com.ivy.import.csvimport.flow.instructions.FortuneCitySteps
import com.ivy.wallet.domain.deprecated.logic.csv.model.ImportType
import com.ivy.import.csvimport.flow.instructions.IvyWalletSteps
import com.ivy.import.csvimport.flow.instructions.KTWMoneyManagerSteps
import com.ivy.import.csvimport.flow.instructions.MonefySteps
import com.ivy.import.csvimport.flow.instructions.MoneyManagerPraseSteps
import com.ivy.import.csvimport.flow.instructions.OneMoneySteps
import com.ivy.import.csvimport.flow.instructions.SpendeeSteps
import com.ivy.import.csvimport.flow.instructions.WalletByBudgetBakersSteps

@Composable
fun ImportType.ImportSteps(
    onUploadClick: () -> Unit
) {
    when (this) {
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
    }
}