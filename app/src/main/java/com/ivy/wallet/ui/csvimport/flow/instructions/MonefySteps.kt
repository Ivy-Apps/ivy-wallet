package com.ivy.wallet.ui.csvimport.flow.instructions

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MonefySteps(
    onUploadClick: () -> Unit
) {
    Spacer(Modifier.height(12.dp))

    //TODO: Implement

    StepTitle(
        number = 1,
        title = "Export CSV file"
    )

    Spacer(Modifier.height(12.dp))

    VideoArticleRow(
        videoUrl = null,
        articleUrl = "https://help.spendee.com/article/137-export-transactions"
    )

    Spacer(Modifier.height(12.dp))

    StepTitle(
        number = 2,
        title = "Check your email's \"Promotions\" and \"Spam\" folders"
    )

    Spacer(Modifier.height(24.dp))

    StepTitle(
        number = 3,
        title = "Download the \"transactions_export...\" file attached to the email.",
        description = "If you have more than one currency you'll have to download each \"transactions_export...\" file and import it in Ivy."
    )

    Spacer(Modifier.height(24.dp))

    UploadFileStep(
        stepNumber = 4,
        onUploadClick = onUploadClick
    )
}