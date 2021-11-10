package com.ivy.wallet.ui.csvimport.flow.instructions

import androidx.compose.runtime.Composable

@Composable
fun IvyWalletSteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        onUploadClick = onUploadClick
    )
}