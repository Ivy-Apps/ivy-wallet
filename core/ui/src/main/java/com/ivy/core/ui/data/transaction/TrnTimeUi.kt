package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalContext
import com.ivy.common.timeNowLocal
import com.ivy.core.ui.time.formatNicely
import java.time.LocalDateTime

@Immutable
sealed interface TrnTimeUi {
    @Immutable
    data class Actual(val actual: String) : TrnTimeUi

    @Immutable
    data class Due(
        val dueOn: String,
        val upcoming: Boolean,
    ) : TrnTimeUi
}

@Composable
fun dummyTrnTimeActualUi(
    time: LocalDateTime = timeNowLocal()
) = TrnTimeUi.Actual(time.formatNicely(LocalContext.current).uppercase())

@Composable
fun dummyTrnTimeDueUi(
    time: LocalDateTime = timeNowLocal().plusHours(1),
    upcoming: Boolean = true,
) = TrnTimeUi.Due(time.formatNicely(LocalContext.current).uppercase(), upcoming = upcoming)