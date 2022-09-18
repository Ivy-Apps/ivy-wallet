package com.ivy.core.domain.action.data

sealed interface Modify<T> {
    data class Save<T>(val items: List<T>) : Modify<T>
    data class Delete<T>(val itemIds: List<String>) : Modify<T>
}