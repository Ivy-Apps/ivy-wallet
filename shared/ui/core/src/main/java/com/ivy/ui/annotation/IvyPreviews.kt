package com.ivy.ui.annotation

import androidx.compose.ui.tooling.preview.Preview

@Suppress("PreviewAnnotationNaming")
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(
    name = "Small Phone",
    device = "id:Nexus One",
)
@Preview(
    name = "Large Phone",
    device = "id:pixel_8_pro",
)
annotation class IvyPreviews