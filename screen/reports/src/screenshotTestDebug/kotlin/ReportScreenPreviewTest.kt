@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.reports.ReportNoFilterUiTest
import com.ivy.reports.ReportUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewReportUiScreenLight() {
    ReportUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewReportUiScreenDark() {
    ReportUiTest(isDark = true)
}

@IvyPreviews
@Composable
private fun PreviewReportNoFilterUiScreenLight() {
    ReportNoFilterUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewReportNoFilterUiScreenDark() {
    ReportNoFilterUiTest(isDark = true)
}