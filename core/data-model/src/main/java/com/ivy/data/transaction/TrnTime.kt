package com.ivy.data.transaction

import java.time.LocalDateTime

sealed interface TrnTime {
    data class Actual(val actual: LocalDateTime) : TrnTime
    data class Due(val due: LocalDateTime) : TrnTime
}

fun dummyTrnTimeActual(
    time: LocalDateTime = LocalDateTime.now()
) = TrnTime.Actual(time)

fun dummyTrnTimeDue(
    time: LocalDateTime = LocalDateTime.now().plusHours(1),
) = TrnTime.Due(time)