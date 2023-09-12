package com.ivy.import.csvimport.flow.instructions

import androidx.compose.runtime.Composable
import com.ivy.import.csvimport.flow.instructions.DefaultImportSteps

@Composable
fun KTWMoneyManagerSteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        onUploadClick = onUploadClick
    )
}
