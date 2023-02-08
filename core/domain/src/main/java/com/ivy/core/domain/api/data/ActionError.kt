package com.ivy.core.domain.api.data

sealed interface ActionError {
    val reason: Throwable

    data class IO(override val reason: Throwable) : ActionError
}