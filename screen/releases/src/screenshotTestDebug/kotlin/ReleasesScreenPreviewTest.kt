@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.releases.ReleaseScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewReleasesScreenLight() {
    ReleaseScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewReleasesScreenDark() {
    ReleaseScreenUiTest(isDark = true)
}