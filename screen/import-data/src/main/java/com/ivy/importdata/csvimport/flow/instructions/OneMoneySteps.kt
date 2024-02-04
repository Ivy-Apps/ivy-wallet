package com.ivy.importdata.csvimport.flow.instructions

import androidx.compose.runtime.Composable

@Composable
fun OneMoneySteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        onUploadClick = onUploadClick
    )
}
