package com.ivy.core.ui.data.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalContext
import com.ivy.common.time.deviceTimeProvider
import com.ivy.common.time.format
import com.ivy.common.time.timeNow
import com.ivy.core.ui.time.formatNicely
import java.time.LocalDateTime

@Immutable
sealed interface TrnTimeUi {
    @Immutable
    data class Actual(
        val actualDate: String,
        val actualTime: String,
    ) : TrnTimeUi

    @Immutable
    data class Due(
        val dueOnDate: String,
        val dueOnTime: String,
        val upcoming: Boolean,
    ) : TrnTimeUi
}

@Composable
fun dummyTrnTimeActualUi(
    time: LocalDateTime = timeNow()
) = TrnTimeUi.Actual(
    actualDate = time.formatNicely(
        LocalContext.current,
        deviceTimeProvider(),
    ).uppercase(),
    actualTime = time.format("HH:mm"),
)

@Composable
fun dummyTrnTimeDueUi(
    time: LocalDateTime = timeNow().plusHours(1),
    upcoming: Boolean = true,
) = TrnTimeUi.Due(
    dueOnDate = time.formatNicely(
        LocalContext.current,
        deviceTimeProvider()
    ).uppercase(),
    dueOnTime = time.format("HH:mm"),
    upcoming = upcoming
)