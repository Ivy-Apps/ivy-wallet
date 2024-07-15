package com.ivy.ui.testing

import androidx.compose.ui.tooling.preview.Preview

@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.FUNCTION
)
@Preview(
    apiLevel = 34,
    name = "Medium Phone",
    device = "spec:id=reference_phone,shape=Normal,width=411,height=891,unit=dp,dpi=420",
)
@Preview(
    apiLevel = 34,
    name = "Large Phone",
    device = "id:pixel_8_pro",
)
annotation class IvyPreviews