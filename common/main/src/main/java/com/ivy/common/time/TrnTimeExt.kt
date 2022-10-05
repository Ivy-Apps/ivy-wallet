package com.ivy.common.time

import com.ivy.data.transaction.TrnTime
import java.time.LocalDateTime

fun TrnTime.time(): LocalDateTime = when (this) {
    is TrnTime.Actual -> actual
    is TrnTime.Due -> due
}