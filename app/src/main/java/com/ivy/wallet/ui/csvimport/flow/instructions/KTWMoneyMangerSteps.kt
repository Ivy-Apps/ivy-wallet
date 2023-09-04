package com.ivy.wallet.ui.csvimport.flow.instructions

import androidx.compose.runtime.Composable

@Composable
fun KTWMoneyManagerSteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        onUploadClick = onUploadClick
    )
}
