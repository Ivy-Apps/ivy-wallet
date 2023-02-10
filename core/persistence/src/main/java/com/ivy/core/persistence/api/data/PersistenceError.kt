package com.ivy.core.persistence.api.data

sealed interface PersistenceError {
    val reason: Throwable
}