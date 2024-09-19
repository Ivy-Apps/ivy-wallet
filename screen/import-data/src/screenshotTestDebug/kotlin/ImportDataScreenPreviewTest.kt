@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.importdata.csvimport.ImportScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewImportScreenLight() {
    ImportScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewImportScreenDark() {
    ImportScreenUiTest(isDark = true)
}