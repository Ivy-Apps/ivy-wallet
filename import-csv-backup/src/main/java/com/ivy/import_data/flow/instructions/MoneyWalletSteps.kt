package com.ivy.wallet.ui.csvimport.flow.instructions

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ivy.base.R
import com.ivy.design.l0_system.UI
import com.ivy.design.l0_system.style
import com.ivy.import_data.flow.instructions.StepTitle
import com.ivy.import_data.flow.instructions.UploadFileStep

@Composable
fun MoneyWalletSteps(
    onUploadClick: () -> Unit
) {
    Spacer(Modifier.height(12.dp))

    StepTitle(
        number = 1,
        title = stringResource(R.string.export_csv_file),
        description = stringResource(R.string.export_csv_moneywallet_description)
    )

    Spacer(Modifier.height(24.dp))

    StepTitle(
        number = 2,
        title = stringResource(R.string.export_csv_moneywallet_rename_transfer_title),
        description = stringResource(R.string.export_csv_moneywallet_rename_transfer_description)
    )

    Spacer(Modifier.height(24.dp))

    UploadFileStep(
        stepNumber = 3,
        onUploadClick = onUploadClick
    )

    Spacer(Modifier.height(24.dp))

    Text(
        modifier = Modifier.padding(start = 32.dp),
        text = stringResource(R.string.export_csv_caveats),
        style = UI.typo.b1.style(
            fontWeight = FontWeight.Black
        )
    )

    StepTitle(
        number = 1,
        title = stringResource(R.string.export_csv_moneywallet_caveat_1)
    )

    StepTitle(
        number = 2,
        title = stringResource(R.string.export_csv_moneywallet_caveat_2)
    )

}