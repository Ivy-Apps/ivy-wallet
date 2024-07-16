@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.disclaimer.DisclaimerScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewDisclaimerScreenLight() {
    DisclaimerScreenUiTest(false)
}

@IvyPreviews
@Composable
private fun PreviewDisclaimerScreenDark() {
    DisclaimerScreenUiTest(true)
}