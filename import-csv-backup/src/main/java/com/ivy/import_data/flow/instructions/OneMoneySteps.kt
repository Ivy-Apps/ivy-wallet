package com.ivy.import_data.flow.instructions

import androidx.compose.runtime.Composable

@Composable
fun OneMoneySteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        onUploadClick = onUploadClick
    )
}