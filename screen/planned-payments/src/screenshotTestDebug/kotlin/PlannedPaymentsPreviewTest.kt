@file:Suppress("UnusedPrivateMember")

import androidx.compose.runtime.Composable
import com.ivy.planned.edit.EditPlannedScreenUiTest
import com.ivy.planned.list.PlannedPaymentScreenUiTest
import com.ivy.ui.annotation.IvyPreviews

@IvyPreviews
@Composable
private fun PreviewEditPlannedScreenLight() {
    EditPlannedScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewEditPlannedScreenDark() {
    EditPlannedScreenUiTest(isDark = true)
}

@IvyPreviews
@Composable
private fun PreviewPlannedPaymentScreenLight() {
    PlannedPaymentScreenUiTest(isDark = false)
}

@IvyPreviews
@Composable
private fun PreviewPlannedPaymentScreenDark() {
    PlannedPaymentScreenUiTest(isDark = true)
}