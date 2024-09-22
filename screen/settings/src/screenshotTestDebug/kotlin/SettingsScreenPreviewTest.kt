@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.settings.SettingsUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewSettingsUiScreenLight() {
    SettingsUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewSettingsUiScreenDark() {
    SettingsUiTest(isDark = true)
}