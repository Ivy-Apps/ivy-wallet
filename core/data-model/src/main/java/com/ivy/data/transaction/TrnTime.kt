package com.ivy.data.transaction

import java.time.LocalDateTime

sealed interface TrnTime {
    data class Actual(val actual: LocalDateTime) : TrnTime
    data class Due(val due: LocalDateTime) : TrnTime
}