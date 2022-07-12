package com.ivy.import_data.flow.instructions

import androidx.compose.runtime.Composable

@Composable
fun KTWMoneyManagerSteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        onUploadClick = onUploadClick
    )
}