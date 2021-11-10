package com.ivy.wallet.ui.csvimport.flow.instructions

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DefaultImportSteps(
    videoUrl: String? = null,
    articleUrl: String? = null,

    onUploadClick: () -> Unit
) {
    Spacer(Modifier.height(12.dp))

    StepTitle(
        number = 1,
        title = "Export CSV file"
    )

    Spacer(Modifier.height(12.dp))

    VideoArticleRow(
        videoUrl = videoUrl,
        articleUrl = articleUrl
    )

    Spacer(Modifier.height(24.dp))

    UploadFileStep(
        stepNumber = 2,
        onUploadClick = onUploadClick
    )
}