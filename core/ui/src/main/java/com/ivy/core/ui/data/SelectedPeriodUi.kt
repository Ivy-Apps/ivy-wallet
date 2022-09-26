package com.ivy.core.ui.data

import androidx.compose.runtime.Immutable

@Immutable
sealed interface SelectedPeriodUi {
    @Immutable
    data class Monthly(val text: String) : SelectedPeriodUi

    @Immutable
    data class InTheLast(val text: String) : SelectedPeriodUi

    @Immutable
    data class AllTime(val text: String) : SelectedPeriodUi

    @Immutable
    data class CustomRange(val text: String) : SelectedPeriodUi
}