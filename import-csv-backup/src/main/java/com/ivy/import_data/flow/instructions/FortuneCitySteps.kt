package com.ivy.import_data.flow.instructions

import androidx.compose.runtime.Composable

@Composable
fun FortuneCitySteps(
    onUploadClick: () -> Unit
) {
    DefaultImportSteps(
        articleUrl = "https://fourdesire.helpshift.com/hc/en/5-fortune-city/faq/242-can-i-export-my-fortune-city-records/",
        onUploadClick = onUploadClick
    )
}