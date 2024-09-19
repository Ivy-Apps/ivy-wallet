package com.ivy.ui.annotation

import androidx.compose.ui.tooling.preview.Preview

@Suppress("PreviewAnnotationNaming")
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview
@Preview(
    name = "nexus_one",
    device = "id:Nexus One",
)
annotation class IvyPreviews