package com.ivy.base.model

import androidx.annotation.Keep
import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Keep
@Serializable
enum class LoanRecordType {
    INCREASE, DECREASE
}

fun <T> LoanRecordType.processByType(decreaseAction: () -> T, increaseAction: () -> T): T {
    return when (this) {
        LoanRecordType.DECREASE -> decreaseAction()
        LoanRecordType.INCREASE -> increaseAction()
    }
}