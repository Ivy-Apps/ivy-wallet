package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Immutable

@Immutable
interface TrnTimeUi {
    @Immutable
    data class Actual(val actual: String) : TrnTimeUi

    @Immutable
    data class Due(val dueOn: String) : TrnTimeUi
}