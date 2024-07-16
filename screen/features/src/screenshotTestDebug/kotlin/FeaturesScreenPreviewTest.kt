@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.features.FeatureScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewFeaturesScreenLight() {
    FeatureScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewFeaturesScreenDark() {
    FeatureScreenUiTest(isDark = true)
}