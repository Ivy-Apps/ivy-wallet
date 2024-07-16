@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.contributors.ContributorScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewContributorScreenLight() {
    ContributorScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewContributorScreenLDark() {
    ContributorScreenUiTest(isDark = true)
}