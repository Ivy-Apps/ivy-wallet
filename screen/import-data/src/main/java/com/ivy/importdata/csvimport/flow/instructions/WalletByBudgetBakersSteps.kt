package com.ivy.importdata.csvimport.flow.instructions

import androidx.compose.runtime.Composable

@Composable
fun WalletByBudgetBakersSteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        articleUrl = "https://support.budgetbakers.com/hc/en-us/articles/209753325-How-to-EXPORT-transactions-from-Wallet",
        onUploadClick = onUploadClick
    )
}
