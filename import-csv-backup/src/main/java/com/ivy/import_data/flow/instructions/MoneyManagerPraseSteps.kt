package com.ivy.import_data.flow.instructions

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivy.base.R

@Composable
fun MoneyManagerPraseSteps(
    onUploadClick: () -> Unit
) {
    Spacer(Modifier.height(12.dp))

    StepTitle(
        number = 1,
        title = stringResource(R.string.export_excel_file),
    )

    Spacer(Modifier.height(12.dp))

    VideoArticleRow(
        videoUrl = null,
        articleUrl = null
    )

    Spacer(Modifier.height(24.dp))


    StepTitle(
        number = 2,
        title = stringResource(R.string.convert_xls_to_csv),
        description = stringResource(R.string.convert_xls_to_csv_description)
    )

    Spacer(Modifier.height(12.dp))

    val rootScreen = com.ivy.core.ui.temp.rootScreen()
    InstructionButton(
        modifier = Modifier.padding(horizontal = 16.dp),
        icon = null,
        caption = stringResource(R.string.online_csv_converter_free),
        text = "https://www.zamzar.com/converters/document/xls-to-csv/"
    ) {
        rootScreen.openUrlInBrowser("https://www.zamzar.com/converters/document/xls-to-csv/")
    }


    Spacer(Modifier.height(24.dp))


    UploadFileStep(
        stepNumber = 3,
        onUploadClick = onUploadClick
    )
}