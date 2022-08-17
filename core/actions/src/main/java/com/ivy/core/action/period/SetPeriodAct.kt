package com.ivy.core.action.period

import com.ivy.data.Period

/**
 * Sets the current app period considering **start day of month**.
 */
class SetPeriodAct {
    sealed class Input {
        object Month : Input()
        data class Range(val period: Period) : Input()
        object LastN : Input()
    }
}