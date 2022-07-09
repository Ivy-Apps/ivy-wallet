package com.ivy.import.flow.instructions

import androidx.compose.runtime.Composable

@Composable
fun OneMoneySteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        onUploadClick = onUploadClick
    )
}