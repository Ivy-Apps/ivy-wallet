package com.ivy.import.csvimport.flow.instructions

import androidx.compose.runtime.Composable

@Composable
fun OneMoneySteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        onUploadClick = onUploadClick
    )
}
