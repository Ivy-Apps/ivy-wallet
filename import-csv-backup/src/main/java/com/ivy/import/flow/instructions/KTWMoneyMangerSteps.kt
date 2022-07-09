package com.ivy.import.flow.instructions

import androidx.compose.runtime.Composable

@Composable
fun KTWMoneyManagerSteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        onUploadClick = onUploadClick
    )
}