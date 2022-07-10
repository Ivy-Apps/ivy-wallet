package com.ivy.import_data.flow.instructions

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivy.base.R

@Composable
fun SpendeeSteps(
    onUploadClick: () -> Unit
) {
    Spacer(Modifier.height(12.dp))

    StepTitle(
        number = 1,
        title = stringResource(R.string.export_csv_file)
    )

    Spacer(Modifier.height(12.dp))

    VideoArticleRow(
        videoUrl = null,
        articleUrl = "https://help.spendee.com/article/137-export-transactions"
    )

    Spacer(Modifier.height(12.dp))

    StepTitle(
        number = 2,
        title = stringResource(R.string.check_email_spam)
    )

    Spacer(Modifier.height(24.dp))

    StepTitle(
        number = 3,
        title = stringResource(R.string.download_email_file),
        description = stringResource(R.string.download_email_file_description)
    )

    Spacer(Modifier.height(24.dp))

    UploadFileStep(
        stepNumber = 4,
        onUploadClick = onUploadClick
    )
}