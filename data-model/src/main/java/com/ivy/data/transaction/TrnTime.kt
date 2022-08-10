package com.ivy.data.transaction

import java.time.LocalDateTime

sealed class TrnTime {
    data class ActualTime(val actual: LocalDateTime): TrnTime()
    data class DueTime(val due: LocalDateTime) : TrnTime()
}