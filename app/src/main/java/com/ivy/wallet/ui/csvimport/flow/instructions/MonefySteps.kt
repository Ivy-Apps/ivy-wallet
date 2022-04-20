package com.ivy.wallet.ui.csvimport.flow.instructions

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivy.wallet.R

@Composable
fun MonefySteps(
    onUploadClick: () -> Unit
) {
    Spacer(Modifier.height(12.dp))

    StepTitle(
        number = 1,
        title = stringResource(R.string.export_to_file),
        description = stringResource(R.string.export_to_file_description)
    )

    Spacer(Modifier.height(24.dp))

    UploadFileStep(
        stepNumber = 2,
        onUploadClick = onUploadClick
    )
}